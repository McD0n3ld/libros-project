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

import edu.upc.eetac.dsa.raul.libros.api.BadRequestException;
import edu.upc.eetac.dsa.raul.libros.api.links.LibrosAPILinkBuilder;
import edu.upc.eetac.dsa.raul.libros.api.model.Libro;
import edu.upc.eetac.dsa.raul.libros.api.model.LibroCollection;

@Path("/libros")
public class LibroResource {

	@Context
	private UriInfo uriInfo;

	@Context
	private SecurityContext security;

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	LibroCollection libros = new LibroCollection();

	
	@GET
	@Path("/{libroid}")
	@Produces(MediaType.LIBROS_API_LIBRO)
	public Response getLibro(@PathParam("libroid") String libroid, @Context Request req) {
		// Create CacheControl
		CacheControl cc = new CacheControl();
		
		Libro libro = new Libro();
		
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		
		try {
			stmt = con.createStatement();
			String query = "SELECT * FROM libros WHERE libroid=" + libroid + ";";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				libro.setLibroid(rs.getInt("libroid"));
				libro.setTitulo(rs.getString("titulo"));
				libro.setAutor(rs.getString("autor"));
				libro.setLengua(rs.getString("lengua"));
				libro.setEdicion(rs.getString("edicion"));
				libro.setFecha_edicion(rs.getDate("fecha_edicion"));
				libro.setFecha_impresion(rs.getDate("fecha_impresion"));
				libro.setEditorial(rs.getString("editorial"));
				libro.setLastUpdate(rs.getTimestamp("lastUpdate"));
				
				libro.addLink(LibrosAPILinkBuilder.buildURILibroId(uriInfo, libro.getLibroid() - 1, "prev"));
				libro.addLink(LibrosAPILinkBuilder.buildURILibroId(uriInfo, libro.getLibroid(), "self"));
				libro.addLink(LibrosAPILinkBuilder.buildURILibroId(uriInfo, libro.getLibroid() + 1, "next"));
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

		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(Integer.toString(libro.getLastUpdate().hashCode()));

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
		rb = Response.ok(libro).cacheControl(cc).tag(eTag);

		return rb.build();
	}
	
	@POST
	@Consumes(MediaType.LIBROS_API_LIBRO)
	@Produces(MediaType.LIBROS_API_LIBRO)
	public Libro createLibro(Libro libro) {
		if (libro.getTitulo().length() > 100)
			throw new BadRequestException("Titulo length must be less or equal than 100 characters");
		if (libro.getAutor().length() > 100)
			throw new BadRequestException("Autor length must be less or equal than 100 characters");
		if (libro.getLengua().length() > 50)
			throw new BadRequestException("Lengua length must be less or equal than 50 characters");
		if (libro.getEdicion().length() > 100)
			throw new BadRequestException("Edicion length must be less or equal than 100 characters");
		if (libro.getEditorial().length() > 100)
			throw new BadRequestException("Editoral length must be less or equal than 100 characters");
		
		Connection con = null;
		Statement stmt = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		try {
			stmt = con.createStatement();
			SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
			String edicion = dt1.format(libro.getFecha_edicion());
			String impresion = dt1.format(libro.getFecha_impresion());
			String update = "insert into libros (titulo, autor, lengua, edicion, fecha_edicion, fecha_impresion, editorial) values ('"+libro.getTitulo()+"', '"+libro.getAutor()+"', '"+libro.getLengua()+"','"+libro.getEdicion()+"','"+edicion+"','"+impresion+"','"+libro.getEditorial()+"');";
			stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int libroid = rs.getInt(1);
				rs.close();

				rs = stmt.executeQuery("SELECT libros.lastUpdate FROM libros WHERE libroid='" + libroid + "';");
				rs.next();
				libro.setLastUpdate(rs.getTimestamp("lastUpdate"));
				libro.setLibroid(libroid);
				libro.addLink(LibrosAPILinkBuilder.buildURILibroId(uriInfo, libro.getLibroid(), "self"));
				libros.add(libro);
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
		return libro;
	}
	
	
	@PUT
	@Path("/{libroid}")
	@Consumes(MediaType.LIBROS_API_LIBRO)
	@Produces(MediaType.LIBROS_API_LIBRO)
	public Libro updateLibro(@PathParam("libroid") String libroid, Libro libro) {
	
		if (libro.getTitulo().length() > 100)
			throw new BadRequestException("Titulo length must be less or equal than 100 characters");
		if (libro.getAutor().length() > 100)
			throw new BadRequestException("Autor length must be less or equal than 100 characters");
		if (libro.getLengua().length() > 50)
			throw new BadRequestException("Lengua length must be less or equal than 50 characters");
		if (libro.getEdicion().length() > 100)
			throw new BadRequestException("Edicion length must be less or equal than 100 characters");
		if (libro.getEditorial().length() > 100)
			throw new BadRequestException("Editoral length must be less or equal than 100 characters");
		
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
			SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
			String edicion = dt1.format(libro.getFecha_edicion());
			String impresion = dt1.format(libro.getFecha_impresion());
			update = "UPDATE libros SET libros.titulo='" + libro.getTitulo() + "', libros.autor='" + libro.getAutor() + "', libros.lengua='" + libro.getLengua() + "', libros.edicion='" + libro.getEdicion() + "', libros.fecha_edicion='" + edicion + "', libros.fecha_impresion='" + impresion + "', libros.editorial='" + libro.getEditorial() + "' WHERE libroid='" + libroid + "';";
			int rows = stmt.executeUpdate(update);
			if (rows == 0)
				throw new LibroNotFoundException();
	
			String query = "SELECT * FROM libros WHERE libroid=" + libroid + ";";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				libro.setLibroid(rs.getInt("libroid"));
				libro.setTitulo(rs.getString("titulo"));
				libro.setAutor(rs.getString("autor"));
				libro.setLengua(rs.getString("lengua"));
				libro.setEdicion(rs.getString("edicion"));
				libro.setFecha_edicion(rs.getDate("fecha_edicion"));
				libro.setFecha_impresion(rs.getDate("fecha_impresion"));
				libro.setEditorial(rs.getString("editorial"));
				libro.setLastUpdate(rs.getTimestamp("lastUpdate"));
				libro.addLink(LibrosAPILinkBuilder.buildURILibroId(uriInfo, libro.getLibroid(), "self"));
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
		return libro;
	}
	
	
	@DELETE
	@Path("/{libroid}")
	public void deleteLibro(@PathParam("libroid") String libroid) {
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
			String query = "DELETE FROM libros WHERE libroid=" + libroid + ";";
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
	@Path("/search")
	@Produces(MediaType.LIBROS_API_LIBRO_COLLECTION)
	public LibroCollection getSearch(@QueryParam("titulo") String titulo, @QueryParam("autor") String autor, @Context Request req) {
		
		
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
				libro.addLink(LibrosAPILinkBuilder.buildURILibroId(uriInfo, libro.getLibroid(),"self"));
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
