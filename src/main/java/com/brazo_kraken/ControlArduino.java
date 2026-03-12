package com.brazo_kraken;

import com.studiohartman.jamepad.ControllerState;

public class ControlArduino {
    protected ControlXbox controlXbox; // Controlador Xbox
    protected PuertoSerial arduino;    // PuertoSerial conectado al Arduino
    
    // Variable para guardar el estado del ciclo anterior
    private ControllerState estadoAnterior;

    // Constructor
    public ControlArduino() {
        this.controlXbox = new ControlXbox();
        this.arduino = new PuertoSerial();
        this.estadoAnterior = null;
    }

    // Función para enviar datos al Arduino a través del control Xbox
    public void mandarDatos() {
        this.controlXbox.actualizarEstado();
        ControllerState estadoActual = this.controlXbox.getState();

        // --- VALIDACIONES INICIALES ---
        if(estadoActual == null || !this.controlXbox.isConnected()) {
            System.out.println("El control se ha desconectado.");
            return;
        }

        if(!this.arduino.getisOpen()) {
            System.out.println("El Arduino no está abierto.");
            return;
        }

        if (estadoAnterior == null) {
            estadoAnterior = estadoActual;
            return;
        }

        // --- LÓGICA DE ENVÍO DE MOTORES A PASO (SIN CAMBIOS) ---
        processTriggerOnChange("L", "S", estadoAnterior.leftTrigger, estadoActual.leftTrigger);
        processTriggerOnChange("R", "S", estadoAnterior.rightTrigger, estadoActual.rightTrigger);

        // --- ¡NUEVA LÓGICA PARA SERVOS CON JOYSTICKS Y BUMPERS! ---
        
        // Define una "zona muerta" para los joysticks
        final float DEADZONE = 0.25f;

        // --- SERVO 0: Joystick Izquierdo (Eje X) ---
        if (estadoActual.leftStickX > DEADZONE) {
            arduino.enviarDatos("k"); // Mover a la derecha
        } else if (estadoActual.leftStickX < -DEADZONE) {
            arduino.enviarDatos("j"); // Mover a la izquierda
        }

        // --- SERVO 1: Joystick Izquierdo (Eje Y) ---
        // Nota: En jamepad, el eje Y es negativo hacia arriba.
        if (estadoActual.leftStickY < -DEADZONE) {
            arduino.enviarDatos("i"); // Mover hacia arriba
        } else if (estadoActual.leftStickY > DEADZONE) {
            arduino.enviarDatos("m"); // Mover hacia abajo
        }

        // --- SERVO 2: Joystick Derecho (Eje Y) ---
        // Nota: En jamepad, el eje Y es negativo hacia arriba.
        if (estadoActual.rightStickY < -DEADZONE) {
            arduino.enviarDatos("o"); // Mover hacia arriba
        } else if (estadoActual.rightStickY > DEADZONE) {
            arduino.enviarDatos("p"); // Mover hacia abajo
        }

        // --- SERVO 3: Botones LB y RB ---
        // Usamos el método processButtonOnChange porque solo queremos un pulso al presionar.
        processButtonOnChange("q", estadoAnterior.lb, estadoActual.lb); // Mover con LB
        processButtonOnChange("w", estadoAnterior.rb, estadoActual.rb); // Mover con RB
        
        // Al final, actualizamos el estado anterior para el próximo ciclo
        estadoAnterior = estadoActual;
    }

    // Método auxiliar para botones (envía solo al presionar)
    private void processButtonOnChange(String comando, boolean estabaPresionado, boolean estaPresionado) {
        if (!estabaPresionado && estaPresionado) { // Si cambió de NO presionado a SÍ presionado
            System.out.println("Comando de Botón: " + comando);
            arduino.enviarDatos(comando);
        }
    }
    
    // Método auxiliar para los gatillos (envía un comando al presionar y otro al soltar)
    private void processTriggerOnChange(String cmdPresionar, String cmdSoltar, float valorAnterior, float valorActual) {
        boolean estabaPresionado = valorAnterior > 0.1f;
        boolean estaPresionado = valorActual > 0.1f;

        if (!estabaPresionado && estaPresionado) { // Se acaba de presionar
            System.out.println("Gatillo presionado: " + cmdPresionar);
            arduino.enviarDatos(cmdPresionar);
        } else if (estabaPresionado && !estaPresionado) { // Se acaba de soltar
            System.out.println("Gatillo soltado: " + cmdPresionar);
            arduino.enviarDatos(cmdSoltar);
        }
    }
}