package com.inf311.paineldoestudante;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ListEventsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("dados")
    private List<EventItem> dados;

    public boolean isSuccess() { return success; }
    public List<EventItem> getDados() { return dados; }
}