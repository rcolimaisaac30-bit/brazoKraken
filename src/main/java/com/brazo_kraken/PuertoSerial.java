package com.brazo_kraken;

import java.io.InputStream; // Utilizado para recibir informacion del Arduino
import java.io.InputStreamReader; // Utilizado para leer la entrada del usuario
import java.io.OutputStream; // Utilizado para enviar comandos al Arduino
import com.fazecast.jSerialComm.SerialPort; // Utilizado para conectar con el puerto serie
import java.io.BufferedReader; // Utilizado para leer la entrada del usuario

public class PuertoSerial {
    private SerialPort port; // Puerto al que esta conectado el arduino
    private InputStream inpstr; // Utilizado para recibir informacion del Arduino
    private OutputStream outstrm; // Utilizado para enviar comandos al Arduino
    private BufferedReader bufferedInput; // Utilizado para leer la entrada del Arduino
    
    // Constructor
    public PuertoSerial() {
        System.out.println("Iniciando conexion Serial ...");
    }

    // Setters y Getters
    public void setPort(SerialPort port) {
        this.port = port;
    }

    public String getPort() {
        return this.port.getSystemPortName();
    }

    // Funciones para listar los puertos disponibles y seleccionar un puerto
    public boolean detectarArduino() {
        for(SerialPort p : SerialPort.getCommPorts()) {
            if(p.getDescriptivePortName().toLowerCase().contains("arduino")) {
                this.port = p;
                System.out.println("Arduino detectado en: " + p.getSystemPortName());
                return true;
            }
        }
        System.out.println("Arduino no detectado");
        return false;
    }

    public boolean seleccionarPuertoPorNumero(int numero) {
        String nombreBuscado = "COM" + numero;
        SerialPort[] ports = SerialPort.getCommPorts();
        for(SerialPort p : ports) {
            if(p.getSystemPortName().equalsIgnoreCase(nombreBuscado)) {
                this.port = p;
                System.out.println("Puerto seleccionado: " + p.getDescriptivePortName());
                return true;
            }
        }
        throw new IllegalArgumentException("No se encontró el puerto COM" + numero);
    }

    public void listarPuertos() {
        int i = 0;
        SerialPort[] ports = SerialPort.getCommPorts();
        if(ports.length == 0) {
            System.out.println("No hay puertos disponibles");
            return;
        }
        for(SerialPort port: ports) {
            System.out.println(i + ".- " + port.getSystemPortName());
            i++;
        }
    }

    public boolean seleccionarPuertoPorLista(int index) {
        SerialPort[] ports = SerialPort.getCommPorts();
        if(index < 0 || index >= ports.length) { //Envia un mensaje de error si el index no es valido y acaba el programa
            throw new IllegalArgumentException("El index seleccionado no es valido");
        }
        this.port = ports[index];
        System.out.println("Puerto seleccionado: " + this.port.getDescriptivePortName());
        return true;
    }

    // Funciones para obtener informacion del puerto serie
    public boolean getisOpen() {
        return this.port != null && this.port.isOpen();
    }

    public String getPortName() {
        return (this.port != null) ? this.port.getSystemPortName() : "No hay puerto seleccionado";
    }

    // Funciones para configurar el puerto serie, dependiendo de los parametros que se le pasen
    public void configurarPuerto() {
        if(this.port == null) {
            throw new IllegalArgumentException("Index no selecionado");
        }
        this.port.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
    }

    public void configurarPuerto(int bauds) {
        if(this.port == null) {
            throw new IllegalArgumentException("Index no selecionado");
        }
        this.port.setComPortParameters(bauds, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
    }

    public void configurarPuerto(int bauds, int databits) {
        if(this.port == null){
            throw new IllegalArgumentException("Index no selecionado");
        }
        this.port.setComPortParameters(bauds, databits, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
    }

    public void configurarPuerto(int bauds, int databits, int stopbits) {
        if(this.port == null) {
            throw new IllegalArgumentException("Index no selecionado");
        }
        this.port.setComPortParameters(bauds, databits, stopbits, SerialPort.NO_PARITY);
    }

    public void configurarPuerto(int bauds, int bindat, int bitsparo, int paridad) {
        if(this.port == null) {
            throw new IllegalArgumentException("Index no selecionado");
        }
        this.port.setComPortParameters(bauds, bindat, bitsparo, paridad);
    }

    // Funciones para configurar los timeouts del puerto serie
    public void configurarTimeouts(String mode, int timeout) {
        int moodInt = (mode.equals("Leer")) ? SerialPort.TIMEOUT_READ_BLOCKING : SerialPort.TIMEOUT_WRITE_BLOCKING;
        this.port.setComPortTimeouts(moodInt, timeout, 0);
    }

    // Funciones para conectar y desconectar el puerto serie
    public boolean abrirPuerto() {
        if(this.port == null) {
            throw new IllegalArgumentException("Puerto no Abierto");
        }
        boolean open = this.port.openPort();
        if(open) {
            System.out.println("Puerto abierto");
            try { 
                this.inpstr = this.port.getInputStream();
                this.outstrm = this.port.getOutputStream();
                this.bufferedInput = new BufferedReader(new InputStreamReader(this.inpstr, "UTF-8"));
            } catch(Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        else {
            System.out.println("No se pudo abrir el puerto");
            return false;
        }
    }

    public void cerrarPuerto() {
        try {
            if(this.bufferedInput != null) this.bufferedInput.close();
            if(this.inpstr != null) this.inpstr.close();
            if(this.outstrm != null) this.outstrm.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        if(this.port != null && this.port.isOpen()) {
            this.port.closePort();
            System.out.println("Puerto cerrado");
        }
    }
    
    // Funciones para enviar y recibir datos
    public void enviarDatos(String message) {
        if(this.outstrm == null) {
            System.out.println("Error: el puerto no está abierto.");
            return;
        }
        try {
            this.outstrm.write((message.trim() + "\n").getBytes());
            this.outstrm.flush(); 
            System.out.println("Mensaje enviado: " + message);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String recibirDatos() {
        try {
            if(this.bufferedInput != null) {
                String dato = this.bufferedInput.readLine();
                return dato != null ? dato.trim() : "NO_DATA";
            }
        } catch(Exception e) {
            e.printStackTrace();
            return "SYNTAX_ERROR";
        }
        return "NO_DATA";
    }
}

// /\_/\
//( o.o )
// > ^ <

// Clase del PuertoSerial