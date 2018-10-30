package ru.icmit.adkazankov.domain;


public abstract class Entity {
    protected Long id;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @Override
    public boolean equals(Object obj) {
        if (id != null || obj == null || obj.getClass() != getClass()) {
            return false;
        }
        Entity e = (Entity) obj;
        Long eId = e.getId();
        return eId != null && eId.equals(id);
    }

}
