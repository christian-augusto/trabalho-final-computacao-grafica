package utils;

import java.util.LinkedList;

public class MyStack<T> {

    private LinkedList<T> list = new LinkedList<T>();;

    public MyStack() {
    }

    public boolean isEmpty() {
        return this.list.size() == 0;
    }

    public void insert(T t) {
        this.list.add(t);
    }

    public void replaceTop(T t) {
        if (this.isEmpty()) {
            this.list.add(t);
        } else {
            this.pop();
            this.list.add(t);
        }
    }

    public void push() {
        this.list.add(this.list.peekLast());
    }

    public void pop() {
        if (this.isEmpty()) {
            System.out.println("Error - Empty Stack");
            System.exit(1);
        }
        this.list.remove(this.list.size() - 1);
    }

    public void printStack() {
        if (isEmpty()) {
            System.out.println("Empty Stack");
        } else {
            for (int i = 0; i < this.list.size(); ++i) {
                System.out.println(this.list.get(i).toString());
            }
        }
    }

    public T getTop() {
        if (this.isEmpty()) {
            System.out.println("Error - Empty Stack");
            System.exit(1);
        }
        return this.list.peekLast();
    }

}
