package com.darrenswhite.boozle.util;

import java.io.Serializable;

/**
 * @author Darren White
 */
public interface Function<T, R> extends Serializable {

	R apply(T t);
}