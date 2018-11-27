package com.code.codemercenaries.girdthyswordpro.beans.remote;

/**
 * Created by Joel Kingsley on 27-11-2018.
 */

public class Sword {
    private String id;
    private String name;
    private Integer cost;
    private String path;
    private int statusResourceID;
    private int costResourceID;
    private int cardViewResourceID;
    private boolean equipped;

    public Sword(String id, String name, Integer cost, String path, int statusResourceID, int costResourceID, int cardViewResourceID, boolean equipped) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.path = path;
        this.statusResourceID = statusResourceID;
        this.costResourceID = costResourceID;
        this.cardViewResourceID = cardViewResourceID;
        this.equipped = equipped;
    }

    public Sword(String id, String name, Integer cost, String path, int statusResourceID, int costResourceID, int cardViewResourceID) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.path = path;
        this.statusResourceID = statusResourceID;
        this.costResourceID = costResourceID;
        this.cardViewResourceID = cardViewResourceID;
    }

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

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getStatusResourceID() {
        return statusResourceID;
    }

    public void setStatusResourceID(int statusResourceID) {
        this.statusResourceID = statusResourceID;
    }

    public int getCostResourceID() {
        return costResourceID;
    }

    public void setCostResourceID(int costResourceID) {
        this.costResourceID = costResourceID;
    }

    public int getCardViewResourceID() {
        return cardViewResourceID;
    }

    public void setCardViewResourceID(int cardViewResourceID) {
        this.cardViewResourceID = cardViewResourceID;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }

    @Override
    public String toString() {
        return "Sword{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", cost=" + cost +
                ", path='" + path + '\'' +
                ", statusResourceID=" + statusResourceID +
                ", costResourceID=" + costResourceID +
                ", cardViewResourceID=" + cardViewResourceID +
                ", equipped=" + equipped +
                '}';
    }
}
