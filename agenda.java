import java.io.*;
import java.util.*;

class Contacto {
    private String nombre;
    private String telefono;
    private String correo;
    private String nota;
    private Set<String> etiquetas;

    public Contacto(String nombre, String telefono, String correo, String nota) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.nota = nota;
        this.etiquetas = new HashSet<>();
    }


    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getNota() { return nota; }
    public Set<String> getEtiquetas() { return etiquetas; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setNota(String nota) { this.nota = nota; }
    
    public void agregarEtiqueta(String etiqueta) {
        etiquetas.add(etiqueta.toLowerCase());
    }

    public void eliminarEtiqueta(String etiqueta) {
        etiquetas.remove(etiqueta.toLowerCase());
    }

    @Override
    public String toString() {
        return String.format("Nombre: %s\nTeléfono: %s\nCorreo: %s\nNota: %s\nEtiquetas: %s\n",
                nombre, telefono, correo, nota, String.join(", ", etiquetas));
    }
}

public class agenda {
    private static final Scanner scanner = new Scanner(System.in);
    private List<Contacto> contactos;
    private static final String ARCHIVO_LECTURA = "contactos_lectura.txt";
    private static final String ARCHIVO_ESCRITURA = "contactos_escritura.txt";

    public agenda() {
        contactos = new ArrayList<>();
        cargarContactos();
    }

    private void cargarContactos() {
        File archivoEscritura = new File(ARCHIVO_ESCRITURA);
        if (archivoEscritura.exists()) {
            try {
                List<String> lineas = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_ESCRITURA))) {
                    String linea;
                    while ((linea = reader.readLine()) != null) {
                        lineas.add(linea);
                    }
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_LECTURA))) {
                    for (String linea : lineas) {
                        writer.write(linea);
                        writer.newLine();
                    }
                }

                for (String linea : lineas) {
                    String[] datos = linea.split(",");
                    if (datos.length >= 4) {
                        Contacto contacto = new Contacto(datos[0], datos[1], datos[2], datos[3]);
                        if (datos.length > 4) {
                            Arrays.stream(datos[4].split(";"))
                                  .forEach(contacto::agregarEtiqueta);
                        }
                        contactos.add(contacto);
                    }
                }
                System.out.println("Contactos transferidos de escritura a lectura y cargados en memoria.");
            } catch (IOException e) {
                System.out.println("Error al cargar/transferir los contactos: " + e.getMessage());
            }
        } else {
            System.out.println("No hay contactos previos para cargar.");
            try {
                archivoEscritura.createNewFile();
                new File(ARCHIVO_LECTURA).createNewFile();
            } catch (IOException e) {
                System.out.println("Error al crear los archivos: " + e.getMessage());
            }
        }
    }

    private void guardarContactos() {
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_ESCRITURA))) {
                for (Contacto contacto : contactos) {
                    String etiquetas = String.join(";", contacto.getEtiquetas());
                    writer.write(String.format("%s,%s,%s,%s,%s\n",
                            contacto.getNombre(),
                            contacto.getTelefono(),
                            contacto.getCorreo(),
                            contacto.getNota(),
                            etiquetas));
                }
            }
            System.out.println("Contactos guardados en archivo de escritura.");

            try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_ESCRITURA));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_LECTURA))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    writer.write(linea);
                    writer.newLine();
                }
            }
            System.out.println("Contactos copiados al archivo de lectura.");

        } catch (IOException e) {
            System.out.println("Error al guardar los contactos: " + e.getMessage());
        }
    }

    public void agregarContacto(Contacto contacto) {
        contactos.add(contacto);
        guardarContactos();
    }

    public boolean eliminarContacto(String nombre) {
        boolean eliminado = contactos.removeIf(c -> c.getNombre().equalsIgnoreCase(nombre));
        if (eliminado) {
            guardarContactos();
        }
        return eliminado;
    }

    public Contacto buscarContacto(String nombre) {
        return contactos.stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    public List<Contacto> filtrarPorEtiqueta(String etiqueta) {
        return contactos.stream()
                .filter(c -> c.getEtiquetas().contains(etiqueta.toLowerCase()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<Contacto> obtenerTodos() {
        return new ArrayList<>(contactos);
    }

    public static void main(String[] args) {
        agenda miAgenda = new agenda();
        while (true) {
            mostrarMenu();
            int opcion = leerOpcion();
            
            switch (opcion) {
                case 1:
                    agregarContacto(miAgenda);
                    break;
                case 2:
                    editarContacto(miAgenda);
                    break;
                case 3:
                    eliminarContacto(miAgenda);
                    break;
                case 4:
                    buscarContacto(miAgenda);
                    break;
                case 5:
                    filtrarPorEtiqueta(miAgenda);
                    break;
                case 6:
                    mostrarTodos(miAgenda);
                    break;
                case 7:
                    System.out.println("¡Hasta luego!");
                    return;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n=== AGENDA DE CONTACTOS ===");
        System.out.println("1. Agregar contacto");
        System.out.println("2. Editar contacto");
        System.out.println("3. Eliminar contacto");
        System.out.println("4. Buscar contacto");
        System.out.println("5. Filtrar por etiqueta");
        System.out.println("6. Mostrar todos");
        System.out.println("7. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static void agregarContacto(agenda miAgenda) {
        System.out.println("\n== Nuevo Contacto ==");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Teléfono: ");
        String telefono = scanner.nextLine();
        System.out.print("Correo: ");
        String correo = scanner.nextLine();
        System.out.print("Nota: ");
        String nota = scanner.nextLine();

        Contacto contacto = new Contacto(nombre, telefono, correo, nota);

        System.out.print("Etiquetas (separadas por coma): ");
        String[] etiquetas = scanner.nextLine().split(",");
        for (String etiqueta : etiquetas) {
            if (!etiqueta.trim().isEmpty()) {
                contacto.agregarEtiqueta(etiqueta.trim());
            }
        }

        miAgenda.agregarContacto(contacto);
        System.out.println("Contacto agregado exitosamente");
    }

    private static void editarContacto(agenda miAgenda) {
        System.out.print("\nIngrese el nombre del contacto a editar: ");
        String nombre = scanner.nextLine();
        
        Contacto contacto = miAgenda.buscarContacto(nombre);
        if (contacto == null) {
            System.out.println("Contacto no encontrado");
            return;
        }

        System.out.println("\nDatos actuales:");
        System.out.println(contacto);

        System.out.println("\nIngrese los nuevos datos (presione Enter para mantener el valor actual):");
        
        System.out.print("Nuevo nombre: ");
        String nuevoNombre = scanner.nextLine();
        if (!nuevoNombre.isEmpty()) contacto.setNombre(nuevoNombre);

        System.out.print("Nuevo teléfono: ");
        String nuevoTelefono = scanner.nextLine();
        if (!nuevoTelefono.isEmpty()) contacto.setTelefono(nuevoTelefono);

        System.out.print("Nuevo correo: ");
        String nuevoCorreo = scanner.nextLine();
        if (!nuevoCorreo.isEmpty()) contacto.setCorreo(nuevoCorreo);

        System.out.print("Nueva nota: ");
        String nuevaNota = scanner.nextLine();
        if (!nuevaNota.isEmpty()) contacto.setNota(nuevaNota);

        System.out.print("Nuevas etiquetas (separadas por coma, Enter para mantener las actuales): ");
        String nuevasEtiquetas = scanner.nextLine();
        if (!nuevasEtiquetas.isEmpty()) {
            contacto.getEtiquetas().clear();
            Arrays.stream(nuevasEtiquetas.split(","))
                  .map(String::trim)
                  .filter(e -> !e.isEmpty())
                  .forEach(contacto::agregarEtiqueta);
        }

        miAgenda.guardarContactos();
        System.out.println("Contacto actualizado exitosamente");
    }

    private static void eliminarContacto(agenda miAgenda) {
        System.out.print("\nIngrese el nombre del contacto a eliminar: ");
        String nombre = scanner.nextLine();
        
        if (miAgenda.eliminarContacto(nombre)) {
            System.out.println("Contacto eliminado exitosamente");
        } else {
            System.out.println("Contacto no encontrado");
        }
    }

    private static void buscarContacto(agenda miAgenda) {
        System.out.print("\nIngrese el nombre a buscar: ");
        String nombre = scanner.nextLine();
        
        Contacto contacto = miAgenda.buscarContacto(nombre);
        if (contacto != null) {
            System.out.println("\nContacto encontrado:");
            System.out.println(contacto);
        } else {
            System.out.println("Contacto no encontrado");
        }
    }

    private static void filtrarPorEtiqueta(agenda miAgenda) {
        System.out.print("\nIngrese la etiqueta para filtrar: ");
        String etiqueta = scanner.nextLine();
        
        List<Contacto> filtrados = miAgenda.filtrarPorEtiqueta(etiqueta);
        if (!filtrados.isEmpty()) {
            System.out.println("\nContactos con la etiqueta '" + etiqueta + "':");
            filtrados.forEach(c -> System.out.println("\n" + c));
        } else {
            System.out.println("No se encontraron contactos con esa etiqueta");
        }
    }

    private static void mostrarTodos(agenda miAgenda) {
        List<Contacto> todos = miAgenda.obtenerTodos();
        if (!todos.isEmpty()) {
            System.out.println("\n=== LISTA DE CONTACTOS ===");
            todos.forEach(c -> System.out.println("\n" + c));
        } else {
            System.out.println("\nNo hay contactos en la agenda");
        }
    }
}
