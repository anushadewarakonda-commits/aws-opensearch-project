package org.example.entity;

import java.util.Objects;

public class Category {

    private String id;
    private String name;

    public Category() {
    }

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }


    public Category(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;

    }

    public static class Builder {
            private String id;
            private String name;


            public Builder id (String id){
            this.id = id;
            return this;
        }

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Category build() {
                return new Category(this);
            }
        }
    // Getters and setters, equals, and hashCode methods...


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) && Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
