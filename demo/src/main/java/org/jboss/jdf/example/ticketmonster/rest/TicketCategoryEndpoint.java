package org.jboss.jdf.example.ticketmonster.rest;

import org.jboss.jdf.example.ticketmonster.model.TicketCategory;
import org.jboss.jdf.example.ticketmonster.rest.dto.TicketCategoryDTO;

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
@Path("/ticketcategories")
public class TicketCategoryEndpoint {
    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @POST
    @Consumes("application/json")
    public Response create(TicketCategoryDTO dto) {
        final TicketCategory entity = dto.fromDTO(null, em);
        em.persist(entity);
        return Response.created(UriBuilder.fromResource(TicketCategoryEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
    }

    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    public Response deleteById(@PathParam("id") Long id) {
        final TicketCategory entity = em.find(TicketCategory.class, id);
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
        final TypedQuery<TicketCategory> findByIdQuery = em.createQuery("SELECT DISTINCT t FROM TicketCategory t WHERE t.id = :entityId ORDER BY t.id", TicketCategory.class);
        findByIdQuery.setParameter("entityId", id);
        TicketCategory entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        if (entity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        final TicketCategoryDTO dto = new TicketCategoryDTO(entity);
        return Response.ok(dto).build();
    }

    @GET
    @Produces("application/json")
    public List<TicketCategoryDTO> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
        final TypedQuery<TicketCategory> findAllQuery = em.createQuery("SELECT DISTINCT t FROM TicketCategory t ORDER BY t.id", TicketCategory.class);
        if (startPosition != null) {
            findAllQuery.setFirstResult(startPosition);
        }
        if (maxResult != null) {
            findAllQuery.setMaxResults(maxResult);
        }
        final List<TicketCategory> searchResults = findAllQuery.getResultList();
        final List<TicketCategoryDTO> results = new ArrayList<TicketCategoryDTO>();
        for (TicketCategory searchResult : searchResults) {
            final TicketCategoryDTO dto = new TicketCategoryDTO(searchResult);
            results.add(dto);
        }
        return results;
    }

    @PUT
    @Path("/{id:[0-9][0-9]*}")
    @Consumes("application/json")
    public Response update(@PathParam("id") Long id, TicketCategoryDTO dto) {
        final TypedQuery<TicketCategory> findByIdQuery = em.createQuery("SELECT DISTINCT t FROM TicketCategory t WHERE t.id = :entityId ORDER BY t.id", TicketCategory.class);
        findByIdQuery.setParameter("entityId", id);
        TicketCategory entity;
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