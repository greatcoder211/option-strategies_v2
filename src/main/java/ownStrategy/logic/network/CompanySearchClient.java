package ownStrategy.logic.network;

import ownStrategy.model.entity.portfolio.Company;

import java.util.List;

public interface CompanySearchClient {
    List<Company> getCompanies(String keySearch);
}
