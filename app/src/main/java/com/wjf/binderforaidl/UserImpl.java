package com.wjf.binderforaidl;

import com.wanjf.baseVO.Person;

public interface UserImpl {
    Person getPerson(String name);

    Person[] getPersons();
}
