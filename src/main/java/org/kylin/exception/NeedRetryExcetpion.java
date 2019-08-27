package org.kylin.exception;

public class NeedRetryExcetpion extends Exception{
    public NeedRetryExcetpion(String message) {
        super("Need retry." + message);
    }
}
