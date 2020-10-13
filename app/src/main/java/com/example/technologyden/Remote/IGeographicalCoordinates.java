package com.example.technologyden.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGeographicalCoordinates {

    @GET("maps/api/geocode/json")
    Call<String> getGeographicalCoordinates(@Query("address") String address);

    @GET("maps/api/directions/json")
    Call<String> getDirections(@Query("origin") String origin, @Query("destination") String destination);
}
