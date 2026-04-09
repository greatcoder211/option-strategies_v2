package ownStrategy.mongoDBdto;

import org.springframework.data.annotation.Id;
import ownStrategy.logic.sPattern.Belfort;

public class Train1DTO {
    @Id
    private Belfort position;
    private int counter;
}
