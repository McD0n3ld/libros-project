package edu.upc.eetac.dsa.raul.libros.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import eetac.upc.edu.dsa.raul.libros.api.links.BeeterAPILinkBuilder;
import eetac.upc.edu.dsa.raul.libros.api.model.Sting;
import eetac.upc.edu.dsa.raul.libros.api.model.StingCollection;

@Path("/stings")
public class StingResource {

	@Context
	private UriInfo uriInfo;

	@Context
	private SecurityContext security;

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	StingCollection stings = new StingCollection();

	@GET
	@Produces(MediaType.BEETER_API_STING_COLLECTION)
	public StingCollection getStings(@QueryParam("username") String username, @QueryParam("offset") String offset, @QueryParam("length") String length) {
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

		// Sting for each one and store them in the StingCollection.
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
			stmt = con.createStatement();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		try {
			String query;
			if (username == null)
				query = "select stings.*, users.name FROM stings INNER JOIN users ON (users.username=stings.username) ORDER BY creation_timestamp desc LIMIT "
						+ offset + ", " + length + ";";
			else
				query = "select stings.*, users.name FROM stings INNER JOIN users ON (users.username=stings.username) WHERE stings.username='" + username
						+ "' ORDER BY creation_timestamp DESC LIMIT " + offset + ", " + length + ";";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Sting s = new Sting();
				s.setAuthor(rs.getString("username"));
				s.setContent(rs.getString("content"));
				s.setCreationTimestamp(rs.getTimestamp("creation_timestamp"));
				s.setStingId(rs.getString("stingid"));
				s.setSubject(rs.getString("subject"));
				s.setUsername(rs.getString("username"));
				s.addLink(BeeterAPILinkBuilder.buildURISting(uriInfo, s));
				stings.add(s);
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
		int prev = ioffset - ilength;
		int next = ioffset + ilength;
		stings.addLink(BeeterAPILinkBuilder.buildURIStings(uriInfo, offset, length, username, "self"));
		stings.addLink(BeeterAPILinkBuilder.buildURIStings(uriInfo, Integer.toString(prev), length, username, "prev"));
		stings.addLink(BeeterAPILinkBuilder.buildURIStings(uriInfo, Integer.toString(next), length, username, "next"));
		return stings;
	}

	@POST
	@Consumes(MediaType.BEETER_API_STING)
	@Produces(MediaType.BEETER_API_STING)
	public Sting createSting(Sting sting) {
		// TODO: get connection from database
		if (sting.getSubject().length() > 100)
			throw new BadRequestException("Subject length must be less or equal than 100 characters");
		if (sting.getContent().length() > 500)
			throw new BadRequestException("Content length must be less or equal than 100 characters");
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
			update = "INSERT INTO stings (username,content,subject) values ('" + sting.getUsername() + "','" + sting.getContent() + "','" + sting.getSubject()
					+ "');";
			stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int stingid = rs.getInt(1);
				rs.close();

				// TODO: Retrieve the created sting from the database to get all
				// the remaining fields
				rs = stmt.executeQuery("SELECT stings.creation_timestamp FROM stings WHERE stingid='" + stingid + "';");
				rs.next();
				sting.setCreationTimestamp(rs.getTimestamp("creation_timestamp"));
				sting.setStingId(Integer.toString(stingid));
				sting.addLink(BeeterAPILinkBuilder.buildURIStingId(uriInfo, sting.getStingId(), "self"));
				stings.add(sting);
			} else
				throw new StingNotFoundException();
		} catch (SQLException e) {
			throw new InternalServerException(e.getMessage());
		} finally {
			try {
				con.close();
				stmt.close();
			} catch (Exception e) {
			}
		}
		return sting;
	}

	@GET
	@Path("/{stingid}")
	@Produces(MediaType.BEETER_API_STING)
	public Response getSting(@PathParam("stingid") String stingid, @Context Request req) {
		// Create CacheControl
		CacheControl cc = new CacheControl();
		Sting sting = new Sting();
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();
			String query = "SELECT * FROM stings WHERE stingid=" + stingid + ";";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				sting.setAuthor(rs.getString("username"));
				sting.setContent(rs.getString("content"));
				sting.setCreationTimestamp(rs.getTimestamp("creation_timestamp"));
				sting.setStingId(stingid);
				sting.setSubject(rs.getString("subject"));
				sting.setUsername(rs.getString("username"));
				sting.addLink(BeeterAPILinkBuilder.buildURIStingId(uriInfo, Integer.toString(Integer.parseInt(sting.getStingId()) - 1), "prev"));
				sting.addLink(BeeterAPILinkBuilder.buildURIStingId(uriInfo, sting.getStingId(), "self"));
				sting.addLink(BeeterAPILinkBuilder.buildURIStingId(uriInfo, Integer.toString(Integer.parseInt(sting.getStingId()) + 1), "next"));
			} else
				throw new StingNotFoundException();
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
		EntityTag eTag = new EntityTag(Integer.toString(sting.getCreationTimestamp().hashCode()));

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
		rb = Response.ok(sting).cacheControl(cc).tag(eTag);

		return rb.build();
	}

	@DELETE
	@Path("/{stingid}")
	public void deleteSting(@PathParam("stingid") String stingid) {
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
			String query = "DELETE FROM stings WHERE stingid=" + stingid + ";";
			int rows = stmt.executeUpdate(query);
			if (rows == 0)
				throw new StingNotFoundException();
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

	@PUT
	@Path("/{stingid}")
	@Consumes(MediaType.BEETER_API_STING)
	@Produces(MediaType.BEETER_API_STING)
	public Sting updateSting(@PathParam("stingid") String stingid, Sting sting) {

		if (sting.getSubject().length() > 100)
			throw new BadRequestException("Subject length must be less or equal than 100 characters");
		if (sting.getContent().length() > 500)
			throw new BadRequestException("Content length must be less or equal than 100 characters");
		/*if (security.isUserInRole("registered")) {
			if (!security.getUserPrincipal().getName().equals(sting.getUsername())) {
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
		try {
			stmt = con.createStatement();
			String update;
			/*update = "UPDATE stings SET stings.content='" + sting.getContent() + "', stings.subject='" + sting.getSubject() + "' WHERE stingid='" + stingid
					+ "';";
			int rows = stmt.executeUpdate(update);
			if (rows == 0)
				throw new StingNotFoundException();*/
			String query = "SELECT * FROM stings WHERE stingid=" + stingid + ";";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				sting.setAuthor(rs.getString("username"));
				sting.setContent(rs.getString("content"));
				sting.setCreationTimestamp(rs.getTimestamp("creation_timestamp"));
				sting.setStingId(stingid);
				sting.setSubject(rs.getString("subject"));
				sting.setUsername(rs.getString("username"));
				sting.addLink(BeeterAPILinkBuilder.buildURIStingId(uriInfo, sting.getStingId(), "self"));
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
		return sting;
	}

	@GET
	@Path("/search")
	@Produces(MediaType.BEETER_API_STING_COLLECTION)
	public StingCollection getSearch(@QueryParam("pattern") String pattern, @QueryParam("offset") String offset, @QueryParam("length") String length,
			@Context Request req) {
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

		// TODO: Retrieve all stings stored in the database, instantiate one
		// Sting for each one and store them in the StingCollection.
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
			stmt = con.createStatement();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		try {
			String query = "SELECT stings.*, users.name FROM stings INNER JOIN users ON (users.username=stings.username) WHERE subject LIKE '%" + pattern
					+ "%' OR content LIKE '%" + pattern + "%' ORDER BY creation_timestamp desc LIMIT " + offset + ", " + length + ";";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Sting s = new Sting();
				s.setAuthor(rs.getString("username"));
				s.setContent(rs.getString("content"));
				s.setCreationTimestamp(rs.getTimestamp("creation_timestamp"));
				s.setStingId(rs.getString("stingid"));
				s.setSubject(rs.getString("subject"));
				s.setUsername(rs.getString("username"));
				s.addLink(BeeterAPILinkBuilder.buildURISting(uriInfo, s));
				stings.add(s);
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
		return stings;
	}

}
