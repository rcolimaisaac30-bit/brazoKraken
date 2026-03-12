package com.brazo_kraken;

import java.util.Scanner;

public class App {
    public static void main(String[] args) throws InterruptedException {
        ControlArduino controlArduino = new ControlArduino();
        Scanner scanner = new Scanner(System.in);

        try {
            // Mostrar controles y seleccionar uno
            controlArduino.controlXbox.listarControles();

            System.out.print("Selecciona el ID del control a usar: ");

            if(!controlArduino.controlXbox.seleccionarControlPorLista(0)) {
                System.out.println("No se pudo seleccionar el control.");
                return; // Sale del try, se ejecuta finally para cerrar recursos
            }

            System.out.println("Control seleccionado. Presiona START para salir.");

            // Listar y seleccionar puerto
            if(!controlArduino.arduino.detectarArduino()) {
                controlArduino.arduino.listarPuertos();

                System.out.print("Selecciona el puerto a usar: ");
                int puerto = scanner.nextInt();
                scanner.nextLine();

                try {
                    controlArduino.arduino.seleccionarPuertoPorLista(puerto);
                } catch(IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                    return;
                }
            }

            controlArduino.arduino.configurarPuerto();
            controlArduino.arduino.configurarTimeouts("Leer", 1000); // 1000 ms timeout
            
            if(!controlArduino.arduino.abrirPuerto()) {
                System.out.println("Error al abrir el puerto. Saliendo...");
                return;
            }

            boolean salir = false;
            while(!salir) {
                controlArduino.controlXbox.actualizarEstado(); // Actualiza el estado del controlador
                
                // Imprime el estado del controlador y del Arduino
                if(!controlArduino.controlXbox.isConnected()) {
                    System.out.println("El control se ha desconectado.");
                    break;
                }
                if(!controlArduino.arduino.getisOpen()) {
                    System.out.println("El Arduino no está abierto.");
                    break;
                }

                // Termina el programa si se presiona el botón START
                if(controlArduino.controlXbox.estaPresionado(ControlXbox.Boton.START)) {
                    System.out.println("Botón START presionado. Terminando programa...");
                    salir = true;
                }

                // Envío a Arduino
                controlArduino.mandarDatos();
                Thread.sleep(100);
            }

        } finally {
            scanner.close();
            controlArduino.arduino.cerrarPuerto();
            controlArduino.controlXbox.shutdown();
            System.out.println("Programa terminado.");
        }
    }
}

//mvn exec:java - Para ejecutar el programa
//mvn clean install -U - Para actualizar las dependencias
//mvn dependency:tree - Para ver las dependencias
//mvn dependency:purge-local-repository - Para limpiar las dependencies

// /\_/\
//( o.o )
// > ^ <

// Programa principal

//ejml.org