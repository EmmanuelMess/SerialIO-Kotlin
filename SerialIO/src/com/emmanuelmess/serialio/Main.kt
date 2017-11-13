package com.emmanuelmess.serialio

import jssc.*
import javax.swing.UIManager

object Main {
    val BAUDRATES = listOf(110, 300, 600, 1200, 4800, 9600, 14400, 19200, 38400, 57600, 115200, 128000, 256000)
    val BAUDRATES_NAMES = listOf("110", "300", "600", "1200", "4800", "9600", "14400", "19200",
            "38400", "57600", "115200", "128000", "256000")

    val BITS = listOf(5, 6, 7, 8)
    val BITS_NAMES= listOf("5", "6", "7", "8")

    val STOPBITS = listOf(1, 2, 3)
    val STOPBITS_NAMES= listOf("1", "2", "1,5")

    val PARITY = listOf(0, 1, 2, 3, 4)
    val PARITY_NAMES = listOf("NINGUNA", "PAR", "IMPAR", "MARCADA", "ESPACIADA")

    lateinit var serialPort: SerialPort
    lateinit var serialPort1: SerialPort
    lateinit var c: ChatGUI

    @JvmStatic fun main(args: Array<String>) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        c = ChatGUI("Comunicación serial by EmmanuelMess, Alexey 'scream3r' Sokolov and hasherr") { msg ->
            try {
                serialPort.writeString(msg)
                c.receive("Tx", msg)
            } catch (e: SerialPortException) {
                c.error("Falló al enviar (" + e.getLocalizedMessage() + ")")
            }
        }
        c.preDisplay { baudrateIndex: Int, databitsIndex: Int, stopbitsIndex: Int, parityIndex: Int, com1: String, com2: String ->
            c.display()
            initCOMMs(BAUDRATES[baudrateIndex], BITS[databitsIndex], STOPBITS[stopbitsIndex], PARITY[parityIndex], com1, com2)
        }
    }

    private fun initCOMMs(baudrate: Int, databits:Int, stopbits: Int, parity: Int, com1: String, com2: String) {
        // getting serial ports list into the array
        val portNames: Array<String> = SerialPortList.getPortNames() //Cannot be null


        if (portNames.isEmpty()) {
            c.error("There are no serial ports")
        }

        serialPort = SerialPort (com1)
        try {
            serialPort.openPort()
            serialPort.setParams(baudrate, databits, stopbits, parity)
            serialPort.flowControlMode = SerialPort.FLOWCONTROL_RTSCTS_IN or SerialPort.FLOWCONTROL_RTSCTS_OUT
        } catch (ex: SerialPortException) {
            c.error("There are an error on writing string to port: \n\t" + ex)
        }

        serialPort1 = SerialPort (com2)
        try {
            serialPort1.openPort()
            serialPort.setParams(baudrate, databits, stopbits, parity)
            serialPort1.flowControlMode = SerialPort.FLOWCONTROL_RTSCTS_IN or SerialPort.FLOWCONTROL_RTSCTS_OUT
            serialPort1.addEventListener(PortReader (), SerialPort.MASK_RXCHAR)
        } catch (ex: SerialPortException) {
            c.error("There are an error on writing string to port: \n\t" + ex)
        }
    }

    class PortReader: SerialPortEventListener {
        override fun serialEvent(event: SerialPortEvent) {
            if (event.isRXCHAR && event.eventValue > 0) {
                try {
                    val receivedData = serialPort1.readString (event.eventValue)
                    c.receive("Rx", receivedData)
                } catch (ex: SerialPortException) {
                    System.out.println("Error in receiving string from COM-port:\n\t" + ex)
                }
            }
        }

    }

}