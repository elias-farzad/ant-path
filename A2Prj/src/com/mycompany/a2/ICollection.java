package com.mycompany.a2;

public interface ICollection {
	void add(Object newObject);
	IIterator getIterator();
	int size();
}