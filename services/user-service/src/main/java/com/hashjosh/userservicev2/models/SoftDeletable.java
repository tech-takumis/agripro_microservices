package com.hashjosh.userservicev2.models;

public interface SoftDeletable {
    boolean isDeleted();
    void setDeleted(boolean deleted);
}
