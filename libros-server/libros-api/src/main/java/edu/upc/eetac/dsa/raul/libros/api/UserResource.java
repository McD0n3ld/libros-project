package edu.upc.eetac.dsa.raul.libros.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import eetac.upc.edu.dsa.raul.libros.api.model.User;
import eetac.upc.edu.dsa.raul.libros.api.model.UserCollection;

@Path("/users")
public class UserResource {

	@Context
	private UriInfo uriInfo;
	
	@Context
	private SecurityContext security;

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	UserCollection users = new UserCollection();
	
	@POST
	@Consumes(MediaType.LIBROS_API_USER)
	@Produces(MediaType.LIBROS_API_USER)
	public User createUser(User user) {
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		try {
			stmt = con.createStatement();
			String update; // TODO: create update query
			update = "INSERT INTO users (username,name,email) values ('" + user.getUsername() + "','" + user.getName() + "','" + user.getEmail() + "');";
			stmt.executeUpdate(update);
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				con.close();
				stmt.close();
			} catch (Exception e) {
			}
		}
		return user;
	}

	@GET
	@Path("/{username}")
	@Produces(MediaType.LIBROS_API_USER)
	public User getUser(@PathParam("username") String username, @Context Request req) {
		User user = new User();

		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		try {
			stmt = con.createStatement();
			String query = "SELECT * FROM users WHERE username='" + username + "';";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				user.setUsername(username);
				user.setName(rs.getString("name"));
				user.setEmail(rs.getString("email"));
			} else
				throw new UserNotFoundException();
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				con.close();
				stmt.close();
			} catch (Exception e) {
			}
		}

		return user;
	}

	@PUT
	@Path("/{username}")
	@Consumes(MediaType.LIBROS_API_USER)
	@Produces(MediaType.LIBROS_API_USER)
	public User updateUser(@PathParam("username") String username, User user) {

		if (!user.getEmail().contains("@") || !user.getEmail().contains("."))
			throw new BadRequestException("email introduced not valid");
		
		if (security.isUserInRole("registered")) {
			if (!security.getUserPrincipal().getName().equals(user.getUsername())) {
				throw new ForbiddenException("You are not allowed...");
			}
		} /* else { } //admin */		
		
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		String update = null;
		try {
			stmt = con.createStatement();
			//String update; // TODO: create update query
			update = "UPDATE users SET users.name='" +user.getName() + "', users.email='" + user.getEmail() + "' WHERE users.username='" + username + "';";
			int rows = stmt.executeUpdate(update);
			if (rows == 0)
				throw new UserNotFoundException();
			String query = "SELECT * FROM users WHERE username='" + username + "';";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				user.setName(rs.getString("name"));
				user.setEmail(rs.getString("email"));
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
		return user;
	}
	
	@DELETE
	@Path("/{username}")
	public void deleteUser(@PathParam("username") String username) {
		// TODO Delete record in database stings identified by stingid.
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();
			String query = "DELETE FROM users WHERE username='" + username + "';";
			int rows = stmt.executeUpdate(query);
			if (rows == 0)
				throw new UserNotFoundException();
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
	@Path("/search")
	@Produces(MediaType.LIBROS_API_USER_COLLECTION)
	public UserCollection getSearch(@QueryParam("pattern") String pattern, @QueryParam("offset") String offset, @QueryParam("length") String length, @Context Request req) {
		if ((offset == null) || (length == null))
			throw new BadRequestException("offset and length are mandatory parameters");
		int ioffset, ilength;
		try {
			ioffset = Integer.parseInt(offset);
			if (ioffset < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new BadRequestException("offset must be an integer greater or equal than 0.");
		}
		try {
			ilength = Integer.parseInt(length);
			if (ilength < 1)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			throw new BadRequestException("length must be an integer greater or equal than 0.");
		}

		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
			stmt = con.createStatement();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		try {
			String query = "SELECT * FROM users WHERE username LIKE '%"+pattern+"%' OR name LIKE '%"+pattern+"%' ORDER BY username desc LIMIT "+ offset + ", " + length + ";";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				User u = new User();
				u.setEmail(rs.getString("email"));
				u.setName(rs.getString("name"));
				u.setUsername(rs.getString("username"));
				users.add(u);
			}
			rs.close();
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				con.close();
				stmt.close();
			} catch (Exception e) {
			}
		}
		return users;
	}	
}
