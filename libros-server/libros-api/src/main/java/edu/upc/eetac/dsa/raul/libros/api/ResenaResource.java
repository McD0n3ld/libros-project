package edu.upc.eetac.dsa.raul.libros.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

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

import edu.upc.eetac.dsa.raul.libros.api.links.LibrosAPILinkBuilder;
import edu.upc.eetac.dsa.raul.libros.api.model.Libro;
import edu.upc.eetac.dsa.raul.libros.api.model.LibroCollection;
import edu.upc.eetac.dsa.raul.libros.api.model.Resena;

@Path("/resena")
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
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			stmt = con.createStatement();
			String consulta = "select * from resenas where username='"+resena.getUsername()+"' and libroid='"+resena.getLibroid()+"';";
			ResultSet rs = stmt.executeQuery(consulta);
			if (rs.next())
				throw new ResenaAlreadyFoundException();
			String update = "insert into resenas (libroid, username, content) values ('"+resena.getLibroid()+"', '"+resena.getUsername()+"', '"+resena.getContent()+"');";
			stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
			rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int resenaid = rs.getInt(1);
				rs.close();

				rs = stmt.executeQuery("SELECT users.name, resenas.lastUpdate FROM users, resenas WHERE users.username='"+resena.getUsername()+"' and resenas.resenaid='" + resenaid + "';");
				rs.next();
				resena.setLastUpdate(rs.getTimestamp("lastUpdate"));
				resena.setResenaid(resenaid);
				resena.setName(rs.getString("name"));
				//libro.addLink(LibrosAPILinkBuilder.buildURILibroId(uriInfo, libro.getLibroid(), "self"));
				//libros.add(libro);
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

		/* if (security.isUserInRole("registered")) {
			if (!security.getUserPrincipal().getName().equals(sting.getUsername())) {
				throw new ForbiddenException("You are not allowed...");
			}
		} */
		
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
			update = "UPDATE resenas SET resenas.content='" + resena.getContent() + "'WHERE resenaid='"+resenaid+"';";
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
				//libro.addLink(LibrosAPILinkBuilder.buildURILibroId(uriInfo, libro.getLibroid(), "self"));
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
			String query = "DELETE FROM resenas WHERE resenaid=" + resenaid + ";";
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
	
	/*
	
	
	
	
	
	@GET
	@Path("/search")
	@Produces(MediaType.LIBROS_API_LIBRO_COLLECTION)
	public LibroCollection getSearch(@QueryParam("titulo") String titulo, @QueryParam("autor") String autor, @Context Request req) {
		/*
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
		
		if (titulo == null && autor == null)
			throw new BadRequestException("Titulo and Autor are mandatoy parameters.");
		
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
			stmt = con.createStatement();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		try {
			int i = 0;
			String query_like = "";
			if (titulo != null) {
				titulo.replaceAll("[^A-Za-z\\s]", "");
				String[] vector = titulo.split(" ");
				query_like = "titulo LIKE '%"+vector[0]+"%'";
				for(i=1;i<vector.length;i++)
					query_like += " OR titulo LIKE '%"+vector[i]+"%'";
			}
			if (autor  != null) {
				autor.replaceAll("[^A-Za-z\\s]", "");
				String[] vector = autor.split(" ");
				if (titulo != null) query_like += " OR ";
				query_like += "autor LIKE '%"+vector[0]+"%'";
				for(i=1;i<vector.length;i++)
					query_like += " OR autor LIKE '%"+vector[i]+"%'";
			}
			String query = "SELECT * FROM libros WHERE "+query_like+" ORDER BY lastUpdate;";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Libro libro = new Libro();
				libro.setLibroid(rs.getInt("libroid"));
				libro.setTitulo(rs.getString("titulo"));
				libro.setAutor(rs.getString("autor"));
				libro.setLengua(rs.getString("lengua"));
				libro.setEdicion(rs.getString("edicion"));
				libro.setFecha_edicion(rs.getDate("fecha_edicion"));
				libro.setFecha_impresion(rs.getDate("fecha_impresion"));
				libro.setEditorial(rs.getString("editorial"));
				libro.setLastUpdate(rs.getTimestamp("lastUpdate"));
				libro.addLink(LibrosAPILinkBuilder.buildURISting(uriInfo, libro));
				libros.add(libro);
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
		return libros;
	}
	
	
	/*
	@GET
	@Produces(MediaType.LIBROS_API_LIBRO_COLLECTION)
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

	

	

	

	

	
	*/

}
