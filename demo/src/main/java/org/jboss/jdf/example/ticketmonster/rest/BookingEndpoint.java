package org.jboss.jdf.example.ticketmonster.rest;

import org.jboss.jdf.example.ticketmonster.model.Booking;
import org.jboss.jdf.example.ticketmonster.rest.dto.BookingDTO;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Stateless
@Path("forge/bookings")
public class BookingEndpoint {
    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @POST
    @Consumes("application/json")
    public Response create(BookingDTO dto) {
        final Booking entity = dto.fromDTO(null, em);
        em.persist(entity);
        return Response.created(UriBuilder.fromResource(BookingEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
    }

    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    public Response deleteById(@PathParam("id") Long id) {
        final Booking entity = em.find(Booking.class, id);
        if (entity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        em.remove(entity);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces("application/json")
    public Response findById(@PathParam("id") Long id) {
        final TypedQuery<Booking> findByIdQuery = em.createQuery("SELECT DISTINCT b FROM Booking b LEFT JOIN FETCH b.tickets LEFT JOIN FETCH b.performance WHERE b.id = :entityId ORDER BY b.id", Booking.class);
        findByIdQuery.setParameter("entityId", id);
        Booking entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        if (entity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        final BookingDTO dto = new BookingDTO(entity);
        return Response.ok(dto).build();
    }

    @GET
    @Produces("application/json")
    public List<BookingDTO> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
        final TypedQuery<Booking> findAllQuery = em.createQuery("SELECT DISTINCT b FROM Booking b LEFT JOIN FETCH b.tickets LEFT JOIN FETCH b.performance ORDER BY b.id", Booking.class);
        if (startPosition != null) {
            findAllQuery.setFirstResult(startPosition);
        }
        if (maxResult != null) {
            findAllQuery.setMaxResults(maxResult);
        }
        final List<Booking> searchResults = findAllQuery.getResultList();
        final List<BookingDTO> results = new ArrayList<BookingDTO>();
        for (Booking searchResult : searchResults) {
            final BookingDTO dto = new BookingDTO(searchResult);
            results.add(dto);
        }
        return results;
    }

    @PUT
    @Path("/{id:[0-9][0-9]*}")
    @Consumes("application/json")
    public Response update(@PathParam("id") Long id, BookingDTO dto) {
        final TypedQuery<Booking> findByIdQuery = em.createQuery("SELECT DISTINCT b FROM Booking b LEFT JOIN FETCH b.tickets LEFT JOIN FETCH b.performance WHERE b.id = :entityId ORDER BY b.id", Booking.class);
        findByIdQuery.setParameter("entityId", id);
        Booking entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        entity = dto.fromDTO(entity, em);
        entity = em.merge(entity);
        return Response.noContent().build();
    }
}