package org.jboss.jdf.example.ticketmonster.rest;

import org.jboss.jdf.example.ticketmonster.model.MediaItem;
import org.jboss.jdf.example.ticketmonster.rest.dto.MediaItemDTO;

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
@Path("/mediaitems")
public class MediaItemEndpoint {
    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @POST
    @Consumes("application/json")
    public Response create(MediaItemDTO dto) {
        final MediaItem entity = dto.fromDTO(null, em);
        em.persist(entity);
        return Response.created(UriBuilder.fromResource(MediaItemEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
    }

    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    public Response deleteById(@PathParam("id") Long id) {
        final MediaItem entity = em.find(MediaItem.class, id);
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
        final TypedQuery<MediaItem> findByIdQuery = em.createQuery("SELECT DISTINCT m FROM MediaItem m WHERE m.id = :entityId ORDER BY m.id", MediaItem.class);
        findByIdQuery.setParameter("entityId", id);
        MediaItem entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        if (entity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        final MediaItemDTO dto = new MediaItemDTO(entity);
        return Response.ok(dto).build();
    }

    @GET
    @Produces("application/json")
    public List<MediaItemDTO> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
        final TypedQuery<MediaItem> findAllQuery = em.createQuery("SELECT DISTINCT m FROM MediaItem m ORDER BY m.id", MediaItem.class);
        if (startPosition != null) {
            findAllQuery.setFirstResult(startPosition);
        }
        if (maxResult != null) {
            findAllQuery.setMaxResults(maxResult);
        }
        final List<MediaItem> searchResults = findAllQuery.getResultList();
        final List<MediaItemDTO> results = new ArrayList<MediaItemDTO>();
        for (MediaItem searchResult : searchResults) {
            final MediaItemDTO dto = new MediaItemDTO(searchResult);
            results.add(dto);
        }
        return results;
    }

    @PUT
    @Path("/{id:[0-9][0-9]*}")
    @Consumes("application/json")
    public Response update(@PathParam("id") Long id, MediaItemDTO dto) {
        final TypedQuery<MediaItem> findByIdQuery = em.createQuery("SELECT DISTINCT m FROM MediaItem m WHERE m.id = :entityId ORDER BY m.id", MediaItem.class);
        findByIdQuery.setParameter("entityId", id);
        MediaItem entity;
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