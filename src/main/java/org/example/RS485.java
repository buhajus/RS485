package org.example;


import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class RS485 {
    public static void main(String[] args) {
        String portName = "COM10"; // Replace with the appropriate port name
        int slaveId = 4; // Modbus slave device ID
        int startAddress = 0; // Starting address of the registers
        int numRegisters = 9; // Number of registers to read

        RS485 reader = new RS485();
        reader.readModbusRegisters(portName, slaveId, startAddress, numRegisters);
    }

    public void readModbusRegisters(String portName, int slaveId, int startAddress, int numRegisters) {
        SerialParameters parameters = new SerialParameters();
        parameters.setPortName(portName);
        parameters.setBaudRate(9600);
        parameters.setDatabits(8);
        parameters.setParity("None");
        parameters.setStopbits(1);

        SerialConnection connection = new SerialConnection(parameters);


        ModbusSerialTransaction transaction;
        ReadMultipleRegistersRequest request;
        ReadMultipleRegistersResponse response;

        try {
            connection.open();

            request = new ReadMultipleRegistersRequest(startAddress, numRegisters);
            request.setUnitID(slaveId);

            transaction = new ModbusSerialTransaction(connection);
            transaction.setRequest(request);

            transaction.execute();
            response = (ReadMultipleRegistersResponse) transaction.getResponse();


            if (response != null) {
                HashMap< Integer, Integer> list = new HashMap<>();
                // Read successful, process the response
                for (int i = 0; i < numRegisters; i++) {

                    int registerAddress = startAddress + i;
                    int registerValue = response.getRegisterValue(i);
                    list.put(registerAddress, registerValue);

                    System.out.println("Register " + registerAddress + ": " + registerValue);
                }
                System.out.println(list);
                if(list.get(0) < 200){
                    System.out.println("lower");
                }else {
                    System.out.println("ok");
                }
            } else {
                // Read failed, handle the error
                System.err.println("Modbus read failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }
}
