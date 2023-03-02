package com.engine.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.engine.Cell;

import static com.engine.Const.P_RADIUS;

public class Particle {
    private Vector2 position, prevPosition, acceleration = Vector2.Zero.cpy();
    private int radius;
    private Color color;
    private final int[] coordinates = new int[2];
    private float mass = 1f;

    public Particle(Vector2 position, int radius) {
        this.position = position.cpy();
        this.prevPosition = position.cpy();
        this.radius = radius;
    }

    public void update(float dt) {
        Vector2 velocity = this.position.cpy().sub(this.prevPosition);
        this.prevPosition = this.position.cpy();
        if(velocity.len() > 6 ) prevPosition.add(velocity.cpy().scl(0.5f));
        this.position.add(velocity.add(this.acceleration.scl(dt * dt)));
        this.acceleration.setZero();
    }

    public void accelerate(Vector2 acceleration) {
        this.acceleration.add(acceleration);
    }

    public void setVelocity(Vector2 velocity, float dt) {
        this.prevPosition = position.cpy().sub(velocity.cpy().scl(dt));
    }

    public void addVelocity(Vector2 velocity, float dt) {
        this.prevPosition.sub(velocity.cpy().scl(dt));
    }

    public Vector2 getVelocity(float dt) {
        return this.position.cpy().sub(this.prevPosition).scl(dt);
    }

    public int getRadius() {
        return radius;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getPrevPosition() {
        return prevPosition;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setAcceleration(Vector2 acceleration) {
        this.acceleration = acceleration;
    }

    public void setPrevPosition(Vector2 prevPosition) {
        this.prevPosition = prevPosition;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public int[] getCoordinates(Cell[][] grid) {
        this.coordinates[0] = (int) (this.getPosition().y / (P_RADIUS << 1));
        this.coordinates[1] = (int) (this.getPosition().x / (P_RADIUS << 1));
        if (coordinates[0] < 0) coordinates[0] = 0;
        if (coordinates[1] < 0) coordinates[1] = 0;
        if (coordinates[0] >= grid.length) coordinates[0] = grid.length - 1;
        if (coordinates[1] >= grid[0].length) coordinates[1] = grid[0].length - 1;

        return this.coordinates;
    }
}
