package br.com.invext.customer.domain.enumerated;

import static br.com.invext.customer.util.Constants.SUBJECT_HIRING_LOAN;
import static br.com.invext.customer.util.Constants.SUBJECT_OTHERS;
import static br.com.invext.customer.util.Constants.SUBJECT_PROBLEM_WITH_CARD;
import static br.com.invext.customer.util.Constants.TYPE_CARDS;
import static br.com.invext.customer.util.Constants.TYPE_LOANS;
import static br.com.invext.customer.util.Constants.TYPE_OTHERS;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SquadEnum {

    CARDS(TYPE_CARDS, SUBJECT_PROBLEM_WITH_CARD),
    LOANS(TYPE_LOANS, SUBJECT_HIRING_LOAN),
    OTHERS(TYPE_OTHERS, SUBJECT_OTHERS);

    private final String type;
    private final String subject;
}
