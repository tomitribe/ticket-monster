package org.jboss.jdf.example.ticketmonster.rest;

import org.jboss.jdf.example.ticketmonster.model.Booking;
import org.jboss.jdf.example.ticketmonster.model.Performance;
import org.jboss.jdf.example.ticketmonster.model.SectionAllocation;
import org.jboss.jdf.example.ticketmonster.model.Show;
import org.jboss.jdf.example.ticketmonster.rest.dto.PerformanceDTO;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
@Path("/performances")
public class PerformanceEndpoint {
    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @POST
    @Consumes("application/json")
    public Response create(PerformanceDTO dto) {
        final Performance entity = dto.fromDTO(null, em);
        em.persist(entity);
        return Response.created(UriBuilder.fromResource(PerformanceEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
    }

    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    public Response deleteById(@PathParam("id") Long id) {
        final Performance entity = em.find(Performance.class, id);
        if (entity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        final Show show = entity.getShow();
        show.getPerformances().remove(entity);
        entity.setShow(null);
        this.em.merge(show);
        final List<SectionAllocation> sectionAllocations = findSectionAllocationsByPerformance(entity);
        for (SectionAllocation sectionAllocation : sectionAllocations) {
            this.em.remove(sectionAllocation);
        }
        final List<Booking> bookings = findBookingsByPerformance(entity);
        for (Booking booking : bookings) {
            this.em.remove(booking);
        }
        em.remove(entity);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces("application/json")
    public Response findById(@PathParam("id") Long id) {
        final TypedQuery<Performance> findByIdQuery = em.createQuery("SELECT DISTINCT p FROM Performance p LEFT JOIN FETCH p.show WHERE p.id = :entityId ORDER BY p.id", Performance.class);
        findByIdQuery.setParameter("entityId", id);
        Performance entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        if (entity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        final PerformanceDTO dto = new PerformanceDTO(entity);
        return Response.ok(dto).build();
    }

    @GET
    @Produces("application/json")
    public List<PerformanceDTO> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
        final TypedQuery<Performance> findAllQuery = em.createQuery("SELECT DISTINCT p FROM Performance p LEFT JOIN FETCH p.show ORDER BY p.id", Performance.class);
        if (startPosition != null) {
            findAllQuery.setFirstResult(startPosition);
        }
        if (maxResult != null) {
            findAllQuery.setMaxResults(maxResult);
        }
        final List<Performance> searchResults = findAllQuery.getResultList();
        final List<PerformanceDTO> results = new ArrayList<PerformanceDTO>();
        for (Performance searchResult : searchResults) {
            final PerformanceDTO dto = new PerformanceDTO(searchResult);
            results.add(dto);
        }
        return results;
    }

    @PUT
    @Path("/{id:[0-9][0-9]*}")
    @Consumes("application/json")
    public Response update(@PathParam("id") Long id, PerformanceDTO dto) {
        final TypedQuery<Performance> findByIdQuery = em.createQuery("SELECT DISTINCT p FROM Performance p LEFT JOIN FETCH p.show WHERE p.id = :entityId ORDER BY p.id", Performance.class);
        findByIdQuery.setParameter("entityId", id);
        Performance entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        entity = dto.fromDTO(entity, em);
        entity = em.merge(entity);
        return Response.noContent().build();
    }

    public List<SectionAllocation> findSectionAllocationsByPerformance(Performance performance) {
        final CriteriaQuery<SectionAllocation> criteria = this.em
                .getCriteriaBuilder().createQuery(SectionAllocation.class);
        final Root<SectionAllocation> from = criteria.from(SectionAllocation.class);
        final CriteriaBuilder builder = this.em.getCriteriaBuilder();
        final Predicate performanceIsSame = builder.equal(from.get("performance"), performance);
        return this.em.createQuery(
                criteria.select(from).where(performanceIsSame)).getResultList();
    }

    public List<Booking> findBookingsByPerformance(Performance performance) {
        final CriteriaQuery<Booking> criteria = this.em
                .getCriteriaBuilder().createQuery(Booking.class);
        final Root<Booking> from = criteria.from(Booking.class);
        final CriteriaBuilder builder = this.em.getCriteriaBuilder();
        final Predicate performanceIsSame = builder.equal(from.get("performance"), performance);
        return this.em.createQuery(
                criteria.select(from).where(performanceIsSame)).getResultList();
    }

}