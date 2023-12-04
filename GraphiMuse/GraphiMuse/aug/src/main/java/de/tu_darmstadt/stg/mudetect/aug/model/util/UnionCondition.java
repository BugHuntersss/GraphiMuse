package de.tu_darmstadt.stg.mudetect.aug.model.util;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UnionCondition {
    public int condition = 0;
    public int n = 0;
    public List<Boolean> booleanList = null;

    public UnionCondition(int condition, int n){
        this.condition = condition;
        this.n = n;
        this.booleanList = this.toBooleanList();
    }

    public UnionCondition(List<Boolean> booleans){
        for (Boolean aBoolean : booleans) {
            condition <<= 1;
            condition += aBoolean ? 1 : 0;
        }
        n = booleans.size();
    }

    public List<Boolean> toBooleanList(){
        List<Boolean> seq = new ArrayList<>();
        int temp = condition;
        for(int j = 0; j < n; j++) {
            seq.add(temp % 2 == 1);
            temp = temp >> 1;
        }

        return Lists.reverse(seq);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnionCondition that = (UnionCondition) o;
        return condition == that.condition && n == that.n;
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, n);
    }

    public int trueNumber(){
        return booleanList.stream().filter(aBoolean -> aBoolean).collect(Collectors.toSet()).size();
    }
}
