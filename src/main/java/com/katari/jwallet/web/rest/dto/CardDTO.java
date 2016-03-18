package com.katari.jwallet.web.rest.dto;

import java.time.LocalDate;
import java.io.Serializable;
import java.util.Objects;


/**
 * A DTO for the Card entity.
 */
public class CardDTO implements Serializable {

    private String id;

    private String number;

    private LocalDate openedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDate getOpenedDate() {
        return openedDate;
    }

    public void setOpenedDate(LocalDate openedDate) {
        this.openedDate = openedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CardDTO cardDTO = (CardDTO) o;

        if ( ! Objects.equals(id, cardDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CardDTO{" +
            "id=" + id +
            ", number='" + number + "'" +
            ", openedDate='" + openedDate + "'" +
            '}';
    }
}
