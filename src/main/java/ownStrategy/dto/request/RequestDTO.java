package ownStrategy.dto.request;

import ownStrategy.model.Belfort;

public record RequestDTO (
    int quantity,
    Belfort position,
    String strategyName,
    String keySearch,
    String selectedCompany
){}
