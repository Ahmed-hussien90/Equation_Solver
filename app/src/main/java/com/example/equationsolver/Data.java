package com.example.equationsolver;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "data")
public class Data {
    @PrimaryKey
    public int Did;

    @ColumnInfo(name = "input_text")
    public String inputText;

    @ColumnInfo(name = "solution_text")
    public String solutionText;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] image;

    public int getDid() {
        return Did;
    }

    public void setDid(int did) {
        Did = did;
    }

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public String getSolutionText() {
        return solutionText;
    }

    public void setSolutionText(String solutionText) {
        this.solutionText = solutionText;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}