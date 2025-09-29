package com.G22_BanquetHall.restaurant.management.model;



public class BanquetHall {
    private Long hallId;
    private String packageName;
    private Integer capacity;
    private String facilities;
    private Long supervisorUserId;

    // Constructors
    public BanquetHall() {}

    public BanquetHall(Long hallId, String packageName, Integer capacity, String facilities, Long supervisorUserId) {
        this.hallId = hallId;
        this.packageName = packageName;
        this.capacity = capacity;
        this.facilities = facilities;
        this.supervisorUserId = supervisorUserId;
    }

    // Getters and Setters
    public Long getHallId() { return hallId; }
    public void setHallId(Long hallId) { this.hallId = hallId; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getFacilities() { return facilities; }
    public void setFacilities(String facilities) { this.facilities = facilities; }

    public Long getSupervisorUserId() { return supervisorUserId; }
    public void setSupervisorUserId(Long supervisorUserId) { this.supervisorUserId = supervisorUserId; }
}
