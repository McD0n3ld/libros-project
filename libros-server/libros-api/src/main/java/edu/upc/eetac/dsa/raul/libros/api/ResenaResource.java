package edu.upc.eetac.dsa.raul.libros.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import edu.upc.eetac.dsa.raul.libros.api.links.LibrosAPILinkBuilder;
import edu.upc.eetac.dsa.raul.libros.api.links.ResenasAPILinkBuilder;
import edu.upc.eetac.dsa.raul.libros.api.model.Libro;
import edu.upc.eetac.dsa.raul.libros.api.model.LibroCollection;
import edu.upc.eetac.dsa.raul.libros.api.model.Resena;

@Path("/resena")
//@Path("/libros/{libroid}/resena")
public class ResenaResource {

	@Context
	private UriInfo uriInfo;

	@Context
	private SecurityContext security;

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	@POST
	@Consumes(MediaType.LIBROS_API_RESENA)
	@Produces(MediaType.LIBROS_API_RESENA)
	public Resena createResena(Resena resena) {

		if (resena.getContent().length() > 500)
			throw new BadRequestException("Content length must be less or equal than 500 characters");
		
//		if (security.isUserInRole("registered")) {
//			if (!security.getUserPrincipal().getName().equals(resena.getUsername())) {
//				throw new ForbiddenException("You are not allowed...");
//			}
//		}
		resena.setUsername(security.getUserPrincipal().getName());

		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();
			String consulta = "select * from resenas where username='" + resena.getUsername() + "' and libroid='" + resena.getLibroid() + "';";
			ResultSet rs = stmt.executeQuery(consulta);
			if (rs.next())
				throw new ResenaAlreadyFoundException();
			String update = "insert into resenas (libroid, username, content) values ('" + resena.getLibroid() + "', '" + resena.getUsername() + "', '"
					+ resena.getContent() + "');";
			stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
			System.out.println("HOLA");
			rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int resenaid = rs.getInt(1);
				rs.close();
				rs = stmt.executeQuery("SELECT users.name, resenas.lastUpdate FROM users, resenas WHERE users.username='" + resena.getUsername()
						+ "' and resenas.resenaid='" + resenaid + "';");
				rs.next();
				resena.setLastUpdate(rs.getTimestamp("lastUpdate"));
				resena.setResenaid(resenaid);
				resena.setName(rs.getString("name"));
				resena.addLink(ResenasAPILinkBuilder.buildURIResenaId(uriInfo,
				resena.getResenaid(), "self"));
			} else
				throw new LibroNotFoundException();
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				con.close();
				stmt.close();
			} catch (Exception e) {
			}
		}
		return resena;
	}

	@PUT
	@Path("/{resenaid}")
	@Consumes(MediaType.LIBROS_API_RESENA)
	@Produces(MediaType.LIBROS_API_RESENA)
	public Resena updateResena(@PathParam("resenaid") String resenaid, Resena resena) {

		if (security.isUserInRole("registered")) {
			if (!security.getUserPrincipal().getName().equals(resena.getUsername())) {
				throw new ForbiddenException("You are not allowed...");
			}
		}

		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();
			String update;
			update = "UPDATE resenas SET resenas.content='" + resena.getContent() + "'WHERE resenaid='" + resenaid + "';";
			int rows = stmt.executeUpdate(update);
			if (rows == 0)
				throw new ResenaNotFoundException();

			String query = "SELECT * FROM resenas WHERE resenaid=" + resenaid + ";";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				resena.setResenaid(rs.getInt("resenaid"));
				resena.setLibroid(rs.getInt("libroid"));
				resena.setUsername(rs.getString("username"));
				resena.setLastUpdate(rs.getTimestamp("lastUpdate"));
				resena.setContent(rs.getString("content"));
				//resena.addLink(ResenasAPILinkBuilder.buildURIResenaId(uriInfo, resena.getResenaid(), "self"));
			}
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				con.close();
				stmt.close();
			} catch (Exception e) {
			}
		}
		return resena;
	}

	@DELETE
	@Path("/{resenaid}")
	public void deleteResena(@PathParam("resenaid") String resenaid) {
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		try {
			stmt = con.createStatement();
			
			String query = "SELECT * FROM resenas WHERE resenaid=" + resenaid + ";";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				if (security.isUserInRole("registered")) {
					if (!security.getUserPrincipal().getName().equals(rs.getString("username"))) {
						throw new ForbiddenException("You are not allowed...");
					}
				}
			} else {
				rs.close();
				throw new ResenaNotFoundException();
			}
			
			query = "DELETE FROM resenas WHERE resenaid=" + resenaid + ";";
			int rows = stmt.executeUpdate(query);
			if (rows == 0)
				throw new LibroNotFoundException();
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				con.close();
				stmt.close();
			} catch (Exception e) {
			}
		}
	}
	
	@GET
	@Path("/{resenaid}")
	@Produces(MediaType.LIBROS_API_RESENA)
	public Response getResena(@PathParam("resenaid") String resenaid, @Context Request req){
		// Create CacheControl
		CacheControl cc = new CacheControl();
		
		Resena resena = new Resena();
		
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		
		try {
			stmt = con.createStatement();
			String query = "SELECT * FROM resenas WHERE resenaid=" + resenaid + ";";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				resena.setContent(rs.getString("content"));
				resena.setLastUpdate(rs.getTimestamp("lastUpdate"));
				resena.setLibroid(rs.getInt("libroid"));
				resena.setResenaid(rs.getInt("resenaid"));
				resena.setUsername(rs.getString("username"));		
				resena.addLink(ResenasAPILinkBuilder.buildURIResenaId(uriInfo, Integer.parseInt(resenaid), "self"));
			} else
				throw new ResenaNotFoundException();
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				con.close();
				stmt.close();
			} catch (Exception e) {
			}
		}

		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(Integer.toString(resena.getLastUpdate().hashCode()));

		// Verify if it matched with etag available in http request
		Response.ResponseBuilder rb = req.evaluatePreconditions(eTag);

		// If ETag matches the rb will be non-null;
		// Use the rb to return the response without any further processing
		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}

		// If rb is null then either it is first time request; or resource is
		// modified
		// Get the updated representation and return with Etag attached to it
		rb = Response.ok(resena).cacheControl(cc).tag(eTag);

		return rb.build();
	}

}
