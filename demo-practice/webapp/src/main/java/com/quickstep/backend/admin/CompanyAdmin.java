package com.quickstep.backend.admin;

import com.google.inject.Inject;
import com.quickstep.jooq.generated.enums.UserStatus;
import com.quickstep.jooq.generated.tables.records.CompanyRecord;
import com.quickstep.ui.SessionData;
import com.vaadin.flow.data.provider.QuerySortOrder;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;

import com.google.inject.persist.Transactional;

import javax.inject.Provider;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.quickstep.jooq.generated.Tables.COMPANY;
import static com.quickstep.jooq.generated.Tables.PORTAL_USER;
import static org.jooq.impl.DSL.count;

public class CompanyAdmin {

    @Inject
    private Provider<DSLContext> dslContextProvider;

    @Inject
    private UserAdmin userAdmin;

    public CompanyRecord findCompany(Long companyId) {
        return dslContextProvider.get()
                .selectFrom(COMPANY)
                .where(COMPANY.ID.eq(companyId))
                .fetchOne();
    }

    private List<CompanyRecord> getCompanyUserCount(List<CompanyRecord> companyList) {
        final Result<Record2<String, Integer>> result = dslContextProvider.get()
                .select(COMPANY.NAME, count())
                .from(COMPANY)
                .join(PORTAL_USER)
                .on(PORTAL_USER.COMPANYID.eq(COMPANY.ID))
                .where(PORTAL_USER.ENABLED.eq(true))
                .and(PORTAL_USER.USER_STATUS.eq(UserStatus.Active))
                .and(PORTAL_USER.COMPANYID.eq(COMPANY.ID))
                .and(COMPANY.ENABLED.eq(true))
                .groupBy(COMPANY.NAME)
                .fetch();

        //Todo How we should handle this?
        /*
        for (Record2<String, Integer> r : result) {
            companyList.stream()
                    .filter(c -> c.getName().equals(r.value1()))
                    .forEach(c -> c.setActiveUsers(r.value2().longValue()));
        }*/

        return companyList;
    }

    private List<CompanyRecord> getCompanyAdditionalInfo(List<CompanyRecord> companyList) {
        companyList = getCompanyUserCount(companyList);
        return  companyList;
    }

    public List<CompanyRecord> listAllCompanies() {
        List<CompanyRecord> companyList = dslContextProvider.get()
                .selectFrom(COMPANY)
                .where(COMPANY.ENABLED.eq(true))
                .fetch();

        companyList = companyList.stream()
                .filter(i->i.getEnabled().equals(true))
                .sorted((c1,c2) -> c1.getName().compareTo(c2.getName()))
                .collect(Collectors.toList());

        companyList = getCompanyAdditionalInfo(companyList);

        return companyList;
    }

    public List<CompanyRecord> listAll() {
        return dslContextProvider.get()
                .selectFrom(COMPANY)
                .where(COMPANY.ENABLED.eq(true))
                .fetch();
    }

    public List<CompanyRecord> listAll(int limit, int offset) {
        return dslContextProvider.get()
                .selectFrom(COMPANY)
                .where(COMPANY.ENABLED.eq(true))
                .limit(limit)
                .offset(offset)
                .fetch();
    }

    public List<CompanyRecord> listAll(List<QuerySortOrder> sortOrders) {
        List<CompanyRecord> companyList = listAll();

        companyList = companyList.stream()
                .filter(i->i.getEnabled().equals(true))
                .collect(Collectors.toList());

        companyList = getCompanyAdditionalInfo(companyList);
        return companyList;
    }

    public int countItems() {
        return dslContextProvider.get()
                .select(count())
                .from(COMPANY)
                .where(COMPANY.ENABLED.eq(true))
                .fetchSingleInto(Integer.class);
    }

    public CompanyRecord load(long id) {
        return findCompany(id);
    }

    public CompanyRecord createNew(SessionData currentSession) {
        CompanyRecord c = new CompanyRecord();

        c.setAuthLogId(currentSession.getAuthLog().getId());
        c.setCreated(Instant.now());
        return c;
    }

    public void save(CompanyRecord entity, SessionData currentSession) {
        entity.setAuthLogId(currentSession.getAuthLog().getId());
        if (entity.getId() == null || entity.getId() <= 0) {
            entity.setCreated(Instant.now());
        }
        entity.setUpdated(Instant.now());

        final CompanyRecord companyRecord = dslContextProvider.get().newRecord(COMPANY, entity);

        if (entity.getId() != null && entity.getId() > 0) {
            companyRecord.update();
        } else {
            companyRecord.store();
        }
    }

    @Transactional
    public void delete(CompanyRecord c, SessionData currentSession) {
        userAdmin.getUsersByCompany(c).forEach(u-> {
            u.setEnabled(false);
            userAdmin.save(u, currentSession);
        });

        c.setEnabled(false);
        this.save(c, currentSession);
    }

    public Class<CompanyRecord> getEntityClass() {
        return CompanyRecord.class;
    }

    public List<CompanyRecord> listCompanies(Long companyId) {
        if (companyId == null) {
            return listAllCompanies();
        }

        return dslContextProvider.get().selectFrom(COMPANY)
                .where(COMPANY.ID.eq(companyId).or(COMPANY.PARENT_COMPANY.eq(companyId)))
                .and(COMPANY.ENABLED.isTrue())
                .fetch();
    }

    public Optional<CompanyRecord> findCompanyByName(String name) {
        return dslContextProvider.get()
                .selectFrom(COMPANY)
                .where(COMPANY.NAME.eq(name))
                .fetchOptional();
    }
}
