package org.example;


import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;

import java.util.*;

public class RS485 {
    public static void main(String[] args) {
        String portName = "COM6"; // Replace with the appropriate port name
        int slaveId = 1; // Modbus slave device ID
        int startAddress = 1000; // Starting address of the registers
        int numRegisters = 10; // Number of registers to read
        int[] addresses = {1000, 1001, 1003, 1008}; // number of addresses in slave

        RS485 reader = new RS485();
        reader.readModbusRegisters(portName, slaveId, startAddress, numRegisters, addresses);
    }

    public void readModbusRegisters(String portName, int slaveId, int startAddress, int numRegisters, int[] addresses) {
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
                HashMap<Integer, Double> list = new HashMap<>();


                // Read successful, process the response
                for (int i = 0; i < numRegisters; i++) {


                    int registerAddress = startAddress + i;
                    double registerValue = response.getRegisterValue(i);

                    for (int temp : addresses) {
                        if (registerAddress == temp) {
                            //if address exist in array - add to list
                            list.put(registerAddress, registerValue / 100);
                        }
                    }


                    System.out.println("Register " + registerAddress + ": " + registerValue);
                }
                System.out.println(list);
                if (list.get(startAddress) < 200) {
                    System.out.println("lower");
                } else {
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
