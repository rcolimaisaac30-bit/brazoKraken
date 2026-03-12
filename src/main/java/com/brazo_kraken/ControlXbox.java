package com.brazo_kraken;

import com.studiohartman.jamepad.ControllerManager; // Utilizado para gestionar los controladores
import com.studiohartman.jamepad.ControllerState; // Utilizado para obtener información del controlador

public class ControlXbox {
    private ControllerManager controllerManager; // Controlador al que esta conectado el control Xbox
    private ControllerState state; // Estado actual del controlador
    private int selectedController; // Índice del controlador seleccionado

    public enum Boton { // Enumeración de los botones disponibles
        A, B, X, Y, START, BACK, LB, RB,
        DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT,
        LEFT_STICK_CLICK, RIGHT_STICK_CLICK
    }

    // Constructor
    public ControlXbox() {
        controllerManager = new ControllerManager();
        controllerManager.initSDLGamepad();
        selectedController = -1;
    }

    // Lista los controles conectados
    public void listarControles() {
        int count = controllerManager.getNumControllers();
        System.out.println("Controles conectados: " + count);
        for(int i = 0; i < count; i++) {
            ControllerState tempState = controllerManager.getState(i);
            boolean isConnected = tempState.isConnected;
            System.out.println("ID " + i + ": " + (isConnected ? "Conectado" : "Desconectado"));
        }
    }

    // Seleccionar control por índice
    public boolean seleccionarControlPorLista(int index) {
        if(index >= 0 && index < controllerManager.getNumControllers()) {
            ControllerState tempState = controllerManager.getState(index);
            if(tempState.isConnected) {
                selectedController = index;
                System.out.println("Control seleccionado: " + index);
                return true;
            }
        }
        System.out.println("Control no válido o desconectado");
        return false;
    }

    // Verifica si el control seleccionado está conectado
    public boolean isConnected() {
        actualizarEstado();
        return state != null && state.isConnected;
    }

    // Devuelve el estado del control seleccionado
    public ControllerState getState() {
        if(selectedController < 0 || selectedController >= controllerManager.getNumControllers()) {
            System.out.println("Control no seleccionado o índice fuera de rango.");
            return null;
        }
        return controllerManager.getState(selectedController);
    }

    // Actualiza el boton presionado
    public void actualizarEstado() {
        this.state = getState();
    }

    // Imprime por consola los botones o ejes que están activos
    public void imprimirEstado() {
        actualizarEstado();
        if(state == null || !state.isConnected) {
            System.out.println("Control no conectado.");
            return;
        }

        // Botones
        if(state.a) System.out.println("Botón A presionado");
        if(state.b) System.out.println("Botón B presionado");
        if(state.x) System.out.println("Botón X presionado");
        if(state.y) System.out.println("Botón Y presionado");
        if(state.start) System.out.println("Botón Start presionado");
        if(state.back) System.out.println("Botón Back presionado");
        if(state.lb) System.out.println("Botón LB presionado");
        if(state.rb) System.out.println("Botón RB presionado");
        if(state.leftStickClick) System.out.println("Click joystick izquierdo");
        if(state.rightStickClick) System.out.println("Click joystick derecho");

        // Gatillos
        if(state.leftTrigger > 0.1f) System.out.println("Gatillo izquierdo: " + state.leftTrigger);
        if(state.rightTrigger > 0.1f) System.out.println("Gatillo derecho: " + state.rightTrigger);

        // Joysticks
        if(Math.abs(state.leftStickX) > 0.1f || Math.abs(state.leftStickY) > 0.1f) {
            System.out.println("Joystick izquierdo: X=" + state.leftStickX + ", Y=" + state.leftStickY);
        }
        if(Math.abs(state.rightStickX) > 0.1f || Math.abs(state.rightStickY) > 0.1f) {
            System.out.println("Joystick derecho: X=" + state.rightStickX + ", Y=" + state.rightStickY);
        }

        // D-pad
        if(state.dpadUp) System.out.println("D-pad arriba");
        if(state.dpadDown) System.out.println("D-pad abajo");
        if(state.dpadLeft) System.out.println("D-pad izquierda");
        if(state.dpadRight) System.out.println("D-pad derecha");
    }

    // Permite consultar si un botón específico está presionado
    public boolean estaPresionado(Boton boton) {
        if(state == null) return false;

        return switch (boton) {
            case A -> state.a;
            case B -> state.b;
            case X -> state.x;
            case Y -> state.y;
            case START -> state.start;
            case BACK -> state.back;
            case LB -> state.lb;
            case RB -> state.rb;
            case LEFT_STICK_CLICK -> state.leftStickClick;
            case RIGHT_STICK_CLICK -> state.rightStickClick;
            case DPAD_UP -> state.dpadUp;
            case DPAD_DOWN -> state.dpadDown;
            case DPAD_LEFT -> state.dpadLeft;
            case DPAD_RIGHT -> state.dpadRight;
        };
    }

    // Desactiva el controlador
    public void shutdown() {
        controllerManager.quitSDLGamepad();
    }
}

// /\_/\
//( o.o )
// > ^ <

// Clase del ControlXbox