import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;

// ---------- pila: bugs criticos que congelan la version actual ----------
class BugCritico {
    int idBug;
    String moduloAfectado;
    String severidad;
    String programadorAsignado;

    BugCritico(int idBug, String moduloAfectado, String severidad, String programadorAsignado) {
        this.idBug = idBug;
        this.moduloAfectado = moduloAfectado;
        this.severidad = severidad;
        this.programadorAsignado = programadorAsignado;
    }

    public String toString() {
        return "Bug #" + idBug + " - modulo: " + moduloAfectado + " - severidad: " + severidad
                + " - asignado a: " + programadorAsignado;
    }
}

// ---------- cola: tareas de renderizado/arte programadas en el servidor ----------
class TareaRenderizado {
    int idTarea;
    String nombreAsset;
    int tiempoEstimadoMin;
    String formatoOutput;

    TareaRenderizado(int idTarea, String nombreAsset, int tiempoEstimadoMin, String formatoOutput) {
        this.idTarea = idTarea;
        this.nombreAsset = nombreAsset;
        this.tiempoEstimadoMin = tiempoEstimadoMin;
        this.formatoOutput = formatoOutput;
    }

    public String toString() {
        return "Tarea #" + idTarea + " - asset: " + nombreAsset + " - tiempo est: " + tiempoEstimadoMin
                + " min - formato: " + formatoOutput;
    }
}

// ---------- lista: catalogo de personajes, habilidades y objetos ----------
class EntidadJuego {
    int idEntidad;
    String nombre;
    String tipo;
    int nivelPoder;
    boolean estadoActivo;

    EntidadJuego(int idEntidad, String nombre, String tipo, int nivelPoder, boolean estadoActivo) {
        this.idEntidad = idEntidad;
        this.nombre = nombre;
        this.tipo = tipo;
        this.nivelPoder = nivelPoder;
        this.estadoActivo = estadoActivo;
    }

    public String toString() {
        String estado = estadoActivo ? "activo" : "inactivo";
        return "#" + idEntidad + " " + nombre + " (" + tipo + ") - nivel poder: " + nivelPoder
                + " - " + estado;
    }
}

class HudPanel extends JPanel {
    private final Color accent;

    HudPanel(Color accent) {
        super(new BorderLayout(10, 10));
        this.accent = accent;
        setOpaque(true);
        setBackground(new Color(12, 18, 28));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int margin = 12;

        g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 80));
        g2.setStroke(new BasicStroke(1f));

        g2.drawLine(w / 2, margin, w / 2, h - margin);
        g2.drawLine(margin, h / 2, w - margin, h / 2);

        g2.drawLine(margin, margin, margin + 28, margin);
        g2.drawLine(w - margin, margin, w - margin - 28, margin);
        g2.drawLine(margin, h - margin, margin + 28, h - margin);
        g2.drawLine(w - margin, h - margin, w - margin - 28, h - margin);

        for (int i = 0; i < 7; i++) {
            int offset = 28 + i * 18;
            g2.drawLine(margin + offset, margin, w - margin, margin + offset);
            g2.drawLine(margin, h - margin - offset, w - margin - offset, h - margin);
        }

        g2.setColor(new Color(255, 255, 255, 18));
        g2.drawRoundRect(6, 6, w - 12, h - 12, 12, 12);
        g2.dispose();
    }
}

public class avanceproy extends JFrame {

    // ---------- paleta de colores del tema ----------
    static final Color COLOR_FONDO = new Color(4, 9, 15);
    static final Color COLOR_PANEL = new Color(11, 18, 28);
    static final Color COLOR_PANEL_ALT = new Color(15, 24, 36);
    static final Color COLOR_BUGS = new Color(255, 80, 80);
    static final Color COLOR_BUGS_SUAVE = new Color(24, 12, 15);
    static final Color COLOR_RENDER = new Color(64, 206, 255);
    static final Color COLOR_RENDER_SUAVE = new Color(10, 23, 31);
    static final Color COLOR_CATALOGO = new Color(88, 255, 178);
    static final Color COLOR_CATALOGO_SUAVE = new Color(8, 24, 19);
    static final Color COLOR_TEXTO_OSCURO = new Color(230, 239, 247);
    static final Color COLOR_TEXTO_SECUNDARIO = new Color(164, 182, 201);
    static final Color COLOR_HUD = new Color(104, 221, 255);
    static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 15);
    static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 13);
    static final Font FUENTE_MONO = new Font("Consolas", Font.PLAIN, 13);

    // las tres estructuras del equipo
    Stack<BugCritico> pilaBugs = new Stack<BugCritico>();
    Queue<TareaRenderizado> colaRenderizado = new LinkedList<TareaRenderizado>();
    ArrayList<EntidadJuego> catalogo = new ArrayList<EntidadJuego>();

    DefaultListModel<EntidadJuego> modeloCatalogo = new DefaultListModel<EntidadJuego>();
    JList<EntidadJuego> listaVisualCatalogo = new JList<EntidadJuego>(modeloCatalogo);

    int contadorBug = 1;
    int contadorTarea = 1;
    int contadorEntidad = 1;

    public avanceproy() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            // si Nimbus no esta disponible, se usa el look por defecto
        }

        setTitle("GameDev Studio - Equipo 6");
        setSize(920, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);

        JLabel encabezado = new JLabel("  // GAMEDEV STUDIO //", SwingConstants.LEFT);
        encabezado.setFont(new Font("Segoe UI", Font.BOLD, 22));
        encabezado.setForeground(COLOR_HUD);
        encabezado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_HUD, 2, true),
                new EmptyBorder(12, 12, 12, 12)));
        encabezado.setOpaque(true);
        encabezado.setBackground(new Color(10, 17, 26));

        JTabbedPane pestañas = new JTabbedPane();
        pestañas.setFont(FUENTE_BOTON);
        pestañas.setBackground(COLOR_FONDO);
        pestañas.setForeground(COLOR_TEXTO_OSCURO);
        pestañas.setBorder(BorderFactory.createLineBorder(new Color(35, 49, 70), 1, true));
        pestañas.setTabPlacement(JTabbedPane.TOP);
        pestañas.setOpaque(true);
        pestañas.addTab("Bugs Criticos (Pila)", crearPanelBugs());
        pestañas.addTab("Renderizado (Cola)", crearPanelRenderizado());
        pestañas.addTab("Catalogo (Lista)", crearPanelCatalogo());

        setLayout(new BorderLayout());
        add(encabezado, BorderLayout.NORTH);
        add(pestañas, BorderLayout.CENTER);
    }

    // ---------- helper para bordes con titulo estilizado ----------
    TitledBorder bordeEstilizado(String titulo, Color color) {
        TitledBorder borde = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(color, 1, true), titulo);
        borde.setTitleFont(FUENTE_TITULO);
        borde.setTitleColor(color);
        borde.setBorder(BorderFactory.createLineBorder(color, 1, true));
        return borde;
    }

    // ---------- helper para botones estilizados ----------
    JButton botonEstilizado(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE_BOTON);
        boton.setForeground(Color.WHITE);
        boton.setBackground(color);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.brighter(), 2, true),
                new EmptyBorder(10, 14, 10, 14)));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setRolloverEnabled(true);
        boton.putClientProperty("JButton.buttonType", "roundRect");
        return boton;
    }

    // ---------- helper para area de texto de salida ----------
    JTextArea areaSalida() {
        JTextArea salida = new JTextArea();
        salida.setEditable(false);
        salida.setLineWrap(true);
        salida.setWrapStyleWord(true);
        salida.setFont(FUENTE_MONO);
        salida.setBackground(new Color(9, 14, 20));
        salida.setForeground(new Color(170, 230, 180));
        salida.setCaretColor(new Color(120, 220, 255));
        salida.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 60, 80), 1, true),
                new EmptyBorder(10, 10, 10, 10)));
        return salida;
    }

    // ---------- helper para campos de texto ----------
    JTextField campoEstilizado(int columnas) {
        JTextField campo = new JTextField(columnas);
        campo.setFont(FUENTE_NORMAL);
        campo.setBackground(new Color(20, 28, 38));
        campo.setForeground(COLOR_TEXTO_OSCURO);
        campo.setCaretColor(new Color(120, 220, 255));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 110, 140), 1, true),
                new EmptyBorder(5, 8, 5, 8)));
        return campo;
    }

    // ---------- helper para etiquetas ----------
    JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FUENTE_NORMAL);
        lbl.setForeground(COLOR_TEXTO_SECUNDARIO);
        return lbl;
    }

    void aplicarNeon(JButton boton, Color color) {
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2, true),
                new EmptyBorder(10, 14, 10, 14)));
        boton.setBackground(color.darker());
        boton.setForeground(new Color(245, 250, 255));
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setFocusPainted(false);
    }

    // ===================== PANEL PILA: BUGS CRITICOS =====================

    JPanel crearPanelBugs() {
        JPanel panel = new HudPanel(COLOR_BUGS);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.setBackground(COLOR_PANEL);

        JPanel form = new HudPanel(COLOR_BUGS);
        form.setBackground(COLOR_BUGS_SUAVE);
        form.setBorder(bordeEstilizado("Nuevo bug critico", COLOR_BUGS));
        form.setOpaque(true);
        JTextField campoModulo = campoEstilizado(10);
        JTextField campoSeveridad = campoEstilizado(6);
        JTextField campoProgramador = campoEstilizado(10);
        form.add(etiqueta("Modulo afectado:"));
        form.add(campoModulo);
        form.add(etiqueta("Severidad:"));
        form.add(campoSeveridad);
        form.add(etiqueta("Programador asignado:"));
        form.add(campoProgramador);

        JTextArea salida = areaSalida();
        JScrollPane scroll = new JScrollPane(salida);
        scroll.setBorder(bordeEstilizado("Registro de actividad", COLOR_BUGS));

        JPanel botones = new JPanel(new GridLayout(3, 1, 8, 8));
        botones.setBackground(COLOR_FONDO);
        botones.setBorder(new EmptyBorder(0, 0, 0, 10));
        JButton btnPush = botonEstilizado("Registrar bug (push)", new Color(255, 79, 79));
        JButton btnPop = botonEstilizado("Resolver mas reciente (pop)", new Color(220, 53, 53));
        JButton btnPeek = botonEstilizado("Ver bug pendiente (peek)", new Color(184, 32, 32));
        aplicarNeon(btnPush, new Color(255, 90, 90));
        aplicarNeon(btnPop, new Color(255, 130, 130));
        aplicarNeon(btnPeek, new Color(255, 70, 70));
        botones.add(btnPush);
        botones.add(btnPop);
        botones.add(btnPeek);

        btnPush.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String modulo = campoModulo.getText().trim();
                String severidad = campoSeveridad.getText().trim();
                String programador = campoProgramador.getText().trim();

                if (modulo.isEmpty() || severidad.isEmpty() || programador.isEmpty()) {
                    salida.append("Llena modulo, severidad y programador.\n");
                    return;
                }

                BugCritico bug = new BugCritico(contadorBug, modulo, severidad, programador);
                contadorBug++;
                pilaBugs.push(bug);
                salida.append("Bug registrado: " + bug + "\n");

                campoModulo.setText("");
                campoSeveridad.setText("");
                campoProgramador.setText("");
            }
        });

        btnPop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (pilaBugs.isEmpty()) {
                    salida.append("No hay bugs criticos pendientes.\n");
                } else {
                    BugCritico bug = pilaBugs.pop();
                    salida.append("Resuelto: " + bug + "\n");
                }
            }
        });

        btnPeek.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (pilaBugs.isEmpty()) {
                    salida.append("No hay bugs criticos en este momento.\n");
                } else {
                    salida.append("Bug pendiente actual: " + pilaBugs.peek() + "\n");
                }
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(botones, BorderLayout.WEST);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ===================== PANEL COLA: RENDERIZADO =====================

    JPanel crearPanelRenderizado() {
        JPanel panel = new HudPanel(COLOR_RENDER);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.setBackground(COLOR_PANEL);

        JPanel form = new HudPanel(COLOR_RENDER);
        form.setBackground(COLOR_RENDER_SUAVE);
        form.setBorder(bordeEstilizado("Nueva tarea de renderizado", COLOR_RENDER));
        form.setOpaque(true);
        JTextField campoAsset = campoEstilizado(10);
        JTextField campoTiempo = campoEstilizado(5);
        JTextField campoFormato = campoEstilizado(8);
        form.add(etiqueta("Nombre asset:"));
        form.add(campoAsset);
        form.add(etiqueta("Tiempo est (min):"));
        form.add(campoTiempo);
        form.add(etiqueta("Formato output:"));
        form.add(campoFormato);

        JTextArea salida = areaSalida();
        JScrollPane scroll = new JScrollPane(salida);
        scroll.setBorder(bordeEstilizado("Registro de actividad", COLOR_RENDER));

        JPanel botones = new JPanel(new GridLayout(3, 1, 8, 8));
        botones.setBackground(COLOR_FONDO);
        botones.setBorder(new EmptyBorder(0, 0, 0, 10));
        JButton btnEnqueue = botonEstilizado("Agregar a la cola (enqueue)", new Color(8, 173, 92));
        JButton btnDequeue = botonEstilizado("Procesar siguiente (dequeue)", new Color(33, 204, 122));
        JButton btnFront = botonEstilizado("Ver siguiente (front)", new Color(18, 143, 84));
        aplicarNeon(btnEnqueue, new Color(63, 255, 160));
        aplicarNeon(btnDequeue, new Color(92, 255, 180));
        aplicarNeon(btnFront, new Color(72, 220, 150));
        botones.add(btnEnqueue);
        botones.add(btnDequeue);
        botones.add(btnFront);

        btnEnqueue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String asset = campoAsset.getText().trim();
                String tiempoTexto = campoTiempo.getText().trim();
                String formato = campoFormato.getText().trim();

                if (asset.isEmpty() || tiempoTexto.isEmpty() || formato.isEmpty()) {
                    salida.append("Llena asset, tiempo y formato.\n");
                    return;
                }

                int tiempo;
                try {
                    tiempo = Integer.parseInt(tiempoTexto);
                } catch (NumberFormatException ex) {
                    salida.append("El tiempo estimado debe ser un numero.\n");
                    return;
                }

                TareaRenderizado tarea = new TareaRenderizado(contadorTarea, asset, tiempo, formato);
                contadorTarea++;
                colaRenderizado.add(tarea);
                salida.append("Agregada a la cola: " + tarea + "\n");

                campoAsset.setText("");
                campoTiempo.setText("");
                campoFormato.setText("");
            }
        });

        btnDequeue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (colaRenderizado.isEmpty()) {
                    salida.append("No hay tareas de renderizado pendientes.\n");
                } else {
                    TareaRenderizado tarea = colaRenderizado.poll();
                    salida.append("Procesando: " + tarea + "\n");
                }
            }
        });

        btnFront.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (colaRenderizado.isEmpty()) {
                    salida.append("No hay tareas de renderizado en este momento.\n");
                } else {
                    salida.append("Siguiente en cola: " + colaRenderizado.peek() + "\n");
                }
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(botones, BorderLayout.WEST);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ===================== PANEL LISTA: CATALOGO =====================

    JPanel crearPanelCatalogo() {
        JPanel panel = new HudPanel(COLOR_CATALOGO);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.setBackground(COLOR_PANEL);

        JPanel form = new HudPanel(COLOR_CATALOGO);
        form.setBackground(COLOR_CATALOGO_SUAVE);
        form.setBorder(bordeEstilizado("Nueva entidad del catalogo", COLOR_CATALOGO));
        form.setOpaque(true);
        JTextField campoNombre = campoEstilizado(10);
        JTextField campoTipo = campoEstilizado(8);
        JTextField campoNivel = campoEstilizado(4);
        JCheckBox checkActivo = new JCheckBox("Activo", true);
        checkActivo.setFont(FUENTE_NORMAL);
        checkActivo.setBackground(COLOR_CATALOGO_SUAVE);
        form.add(etiqueta("Nombre:"));
        form.add(campoNombre);
        form.add(etiqueta("Tipo:"));
        form.add(campoTipo);
        form.add(etiqueta("Nivel de poder:"));
        form.add(campoNivel);
        form.add(checkActivo);

        JTextArea salida = areaSalida();
        JScrollPane scrollSalida = new JScrollPane(salida);
        scrollSalida.setBorder(bordeEstilizado("Registro de actividad", COLOR_CATALOGO));

        listaVisualCatalogo.setFont(FUENTE_NORMAL);
        listaVisualCatalogo.setBackground(new Color(15, 22, 30));
        listaVisualCatalogo.setForeground(COLOR_TEXTO_OSCURO);
        listaVisualCatalogo.setSelectionBackground(new Color(89, 199, 133));
        listaVisualCatalogo.setSelectionForeground(new Color(5, 12, 18));
        listaVisualCatalogo.setBorder(BorderFactory.createLineBorder(new Color(92, 255, 168), 2, true));
        JScrollPane scrollLista = new JScrollPane(listaVisualCatalogo);
        scrollLista.setPreferredSize(new Dimension(260, 0));
        scrollLista.setBorder(bordeEstilizado("Catalogo actual", COLOR_CATALOGO));

        JPanel botones = new JPanel(new GridLayout(3, 1, 8, 8));
        botones.setBackground(COLOR_FONDO);
        botones.setBorder(new EmptyBorder(0, 0, 0, 10));
        JButton btnInsert = botonEstilizado("Agregar entidad (insert)", new Color(40, 120, 255));
        JButton btnDelete = botonEstilizado("Eliminar seleccionada (delete)", new Color(22, 90, 220));
        JButton btnFind = botonEstilizado("Buscar por nombre (find)", new Color(15, 68, 180));
        aplicarNeon(btnInsert, new Color(96, 168, 255));
        aplicarNeon(btnDelete, new Color(110, 182, 255));
        aplicarNeon(btnFind, new Color(80, 140, 255));
        botones.add(btnInsert);
        botones.add(btnDelete);
        botones.add(btnFind);

        btnInsert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nombre = campoNombre.getText().trim();
                String tipo = campoTipo.getText().trim();
                String nivelTexto = campoNivel.getText().trim();

                if (nombre.isEmpty() || tipo.isEmpty() || nivelTexto.isEmpty()) {
                    salida.append("Llena nombre, tipo y nivel de poder.\n");
                    return;
                }

                int nivel;
                try {
                    nivel = Integer.parseInt(nivelTexto);
                } catch (NumberFormatException ex) {
                    salida.append("El nivel de poder debe ser un numero.\n");
                    return;
                }

                EntidadJuego entidad = new EntidadJuego(contadorEntidad, nombre, tipo, nivel, checkActivo.isSelected());
                contadorEntidad++;
                catalogo.add(entidad);
                modeloCatalogo.addElement(entidad);
                salida.append("Agregada al catalogo: " + entidad + "\n");

                campoNombre.setText("");
                campoTipo.setText("");
                campoNivel.setText("");
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int indice = listaVisualCatalogo.getSelectedIndex();
                if (indice == -1) {
                    salida.append("Selecciona una entidad del catalogo para eliminarla.\n");
                } else {
                    EntidadJuego eliminada = catalogo.remove(indice);
                    modeloCatalogo.remove(indice);
                    salida.append("Eliminada: " + eliminada + "\n");
                }
            }
        });

        btnFind.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nombreBuscado = campoNombre.getText().trim();
                if (nombreBuscado.isEmpty()) {
                    salida.append("Escribe el nombre a buscar en el campo de arriba.\n");
                    return;
                }
                boolean encontrada = false;
                for (int i = 0; i < catalogo.size(); i++) {
                    EntidadJuego ent = catalogo.get(i);
                    if (ent.nombre.equalsIgnoreCase(nombreBuscado)) {
                        salida.append("Encontrada: " + ent + "\n");
                        encontrada = true;
                    }
                }
                if (!encontrada) {
                    salida.append("No se encontro ninguna entidad con ese nombre.\n");
                }
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(botones, BorderLayout.WEST);
        panel.add(scrollSalida, BorderLayout.CENTER);
        panel.add(scrollLista, BorderLayout.EAST);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                avanceproy ventana = new avanceproy();
                ventana.setVisible(true);
            }
        });
    }
}