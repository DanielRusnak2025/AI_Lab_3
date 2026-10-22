package com.study;

public class Vector {
    public float x,y;

    public Vector(float x, float y ){
        this.x = x;
        this.y = y;
    }

    public void addVector(Vector v){
        this.x += v.x;
        this.y += v.y;
    }

    public void multiplyVector(float m){
        this.x *= m;
        this.y *= m;
    }

    public Vector subVector(Vector v){
        return new Vector(this.x-v.x, this.y-v.y);
    }

    public float lengthVector(){
        return (float) Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public float lengthVector(Vector v){
        return (float) Math.sqrt((this.x-v.x) * (this.x-v.x) + (this.y-v.y) * (this.y-v.y));
    }

    public void limit(float max){
        float len = lengthVector();
        if(len > max) {
            float k = max / len;
            this.x *= k;
            this.y *= k;
        }
    }

    public void setVector(Vector v){
        this.x = v.x;
        this.y = v.y;
    }

}
