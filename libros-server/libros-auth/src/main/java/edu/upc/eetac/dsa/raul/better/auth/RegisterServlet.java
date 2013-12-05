package edu.upc.eetac.dsa.raul.better.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;

/**
 * Servlet implementation class RegisterServlet
 */
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private DataSource ds = null;

	@Override
	public void init() throws ServletException {
		super.init();
		ds = DataSourceSPA.getInstance().getDataSource();
	}

	public RegisterServlet() {
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String action = req.getParameter("action");
		if (action.equals("REGISTRO")) { // VERIFICAR CAJAS
			String username, pass, name, email;
			username = req.getParameter("username");
			pass = req.getParameter("userpass");
			name = req.getParameter("name");
			email = req.getParameter("email");
			try {
				Connection con = ds.getConnection();
				Statement stmt = con.createStatement();
				String update = "INSERT INTO users VALUES('" + username + "',MD5('" + pass + "'),'" + name + "','" + email + "');";

				stmt.executeUpdate(update);
				// if (row == 0)
				update = "INSERT INTO user_roles VALUES('" + username + "','registered');";
				stmt.executeUpdate(update);
				stmt.close();
				con.close();
				// if (row == 0)
			} catch (Exception e) {
				e.printStackTrace();
			}
			postUserBeeterDB(username, name, email);
			// suponemos que todo ha ido bien.
			
			 String url = "/validar.jsp";
			 ServletContext sc = getServletContext();
			 RequestDispatcher rd = sc.getRequestDispatcher(url);
			 rd.forward(req, res);
		} else if (action.equals("LOGIN")) {
			 String username,userpass;
			 Boolean validado = false;
			 username = req.getParameter("username");
			 userpass = req.getParameter("userpass");
			 try {
				Connection con = ds.getConnection();
				Statement stmt = con.createStatement();
				String query = "SELECT userpass FROM users WHERE username='"+username+"';";
				ResultSet rs = stmt.executeQuery(query);
				if(rs.next()) {
					if (MD5class.GetMD5(userpass) == rs.getString("userpass"));
						validado = true;
				}
				stmt.close();
				con.close();
				// if (row == 0)
			} catch (Exception e) {
				e.printStackTrace();
			}
		//	 return true;
			
		} else {
			 String url = "/register.jsp";
			 ServletContext sc = getServletContext();
			 RequestDispatcher rd = sc.getRequestDispatcher(url);
			 rd.forward(req, res);
		}
	}

	private void postUserBeeterDB(String username, String name, String email) {
		HttpHost targetHost = new HttpHost("localhost", 8080, "http");
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials("admin", "admin"));

		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// Generate BASIC scheme object and add it to the local auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(targetHost, basicAuth);

		// Add AuthCache to the execution context
		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credsProvider);

		HttpPost httpPost = new HttpPost("http://localhost:8080/better-api/users");
		httpPost.addHeader("Content-Type", "application/vnd.beeter.api.user+json");
		httpPost.addHeader("Accept", "application/vnd.beeter.api.user+json");

		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("username", username);
		obj.put("email", email);
		String user = obj.toJSONString();

		try {
			httpPost.setEntity(new StringEntity(user));
			CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
			CloseableHttpResponse httpResponse;
			httpResponse = closeableHttpClient.execute(targetHost, httpPost, context);
			HttpEntity entity = httpResponse.getEntity();
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			String line = null;
			while ((line = reader.readLine()) != null)
				System.out.println(line);
			httpResponse.close();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
