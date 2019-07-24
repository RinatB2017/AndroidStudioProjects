package com.boss.sender_acceleration_data;

class BT_exception extends Exception
{
    // Parameterless Constructor
    public BT_exception() {}

    // Constructor that accepts a message
    public BT_exception(String message)
    {
        super(message);
    }
}
