package com.inf311.paineldoestudante;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<StudentData> student = new MutableLiveData<>();
    private final MutableLiveData<List<RegisterData>> historyList = new MutableLiveData<>();

    public LiveData<StudentData> getStudent() {
        return student;
    }
    public void setStudent(StudentData s) {
        student.setValue(s);
    }

    public void setHistory(List<RegisterData> registers) {
        historyList.setValue(registers);
    }

    public LiveData<List<RegisterData>> getHistory() {
        return historyList;
    }
}