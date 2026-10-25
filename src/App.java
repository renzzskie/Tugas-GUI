import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;

/**
 * Aplikasi GUI sederhana: Daftar Tugas Kuliah.
 *
 * Top Level Container : JFrame (kelas ini meng-extend JFrame)
 * Intermediate        : JPanel, JScrollPane
 * Atomic Component    : JLabel, JTextField, JComboBox, JButton, JList
 * Layout Manager      : BorderLayout, FlowLayout, GridLayout
 * Event Handling      : ActionListener, ListSelectionListener, MouseListener (via MouseAdapter)
 */
public class App extends JFrame implements ActionListener, ListSelectionListener {

    // ===== Atomic Component =====
    private JLabel lblJudul = new JLabel("Daftar Tugas Kuliah");
    private JLabel lblTugas = new JLabel("Tugas:");
    private JLabel lblPrioritas = new JLabel("Prioritas:");
    private JLabel lblStatus = new JLabel("Siap. Belum ada tugas.");
    private JTextField tfTugas = new JTextField(18);
    private JComboBox<String> cbPrioritas =
            new JComboBox<>(new String[]{"Tinggi", "Sedang", "Rendah"});
    private JButton btnTambah = new JButton("Tambah");
    private JButton btnHapus = new JButton("Hapus Terpilih");
    private JButton btnBersihkan = new JButton("Bersihkan Semua");

    // Model menyimpan data, JList menampilkan data
    private DefaultListModel<String> model = new DefaultListModel<>();
    private JList<String> listTugas = new JList<>(model);

    private static final String TANDA_SELESAI = "(Selesai) ";

    public App() {
        // ===== Top Level Container =====
        setTitle("Latihan GUI 5C - Daftar Tugas");
        setSize(520, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // tampil di tengah layar
        // Layout Manager diatur pada content pane, bukan langsung pada JFrame
        getContentPane().setLayout(new BorderLayout(10, 10));

        // ===== Intermediate Component: panel judul (NORTH) =====
        JPanel panelJudul = new JPanel(new FlowLayout(FlowLayout.CENTER));
        lblJudul.setFont(new Font("SansSerif", Font.BOLD, 20));
        panelJudul.add(lblJudul);

        // ===== Intermediate Component: scroll pane berisi list (CENTER) =====
        listTugas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollList = new JScrollPane(listTugas);

        // ===== Intermediate Component: panel input (FlowLayout) =====
        JPanel panelInput = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        panelInput.add(lblTugas);
        panelInput.add(tfTugas);
        panelInput.add(lblPrioritas);
        panelInput.add(cbPrioritas);
        panelInput.add(btnTambah);

        // ===== Intermediate Component: panel tombol (GridLayout 1 baris 2 kolom) =====
        JPanel panelTombol = new JPanel(new GridLayout(1, 2, 10, 0));
        panelTombol.add(btnHapus);
        panelTombol.add(btnBersihkan);

        // Panel bawah menggabungkan input, tombol, dan status (GridLayout 3 baris)
        JPanel panelBawah = new JPanel(new GridLayout(3, 1, 0, 5));
        panelBawah.add(panelInput);
        panelBawah.add(panelTombol);
        panelBawah.add(lblStatus);

        // Pasang ke frame
        getContentPane().add(panelJudul, BorderLayout.NORTH);
        getContentPane().add(scrollList, BorderLayout.CENTER);
        getContentPane().add(panelBawah, BorderLayout.SOUTH);

        // ===== Event Handling =====
        // 1) ActionListener (kelas ini sendiri yang mengimplementasi)
        btnTambah.addActionListener(this);
        btnHapus.addActionListener(this);
        btnBersihkan.addActionListener(this);
        tfTugas.addActionListener(this); // tekan Enter di textfield = Tambah

        // 2) ListSelectionListener (kelas ini sendiri yang mengimplementasi)
        listTugas.addListSelectionListener(this);

        // 3) MouseListener lewat MouseAdapter (anonymous class): klik 2x = tandai selesai
        listTugas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    toggleSelesai();
                }
            }
        });
    }

    // ===== Implementasi ActionListener =====
    @Override
    public void actionPerformed(ActionEvent e) {
        Object sumber = e.getSource();

        if (sumber == btnTambah || sumber == tfTugas) {
            tambahTugas();
        } else if (sumber == btnHapus) {
            hapusTugas();
        } else if (sumber == btnBersihkan) {
            bersihkanSemua();
        }
    }

    // ===== Implementasi ListSelectionListener =====
    @Override
    public void valueChanged(ListSelectionEvent e) {
        // getValueIsAdjusting() true = pemilihan masih berubah, abaikan agar tidak dobel
        if (!e.getValueIsAdjusting() && listTugas.getSelectedValue() != null) {
            lblStatus.setText("Dipilih: " + listTugas.getSelectedValue());
        }
    }

    // ===== Method bantu =====
    private void tambahTugas() {
        String teks = tfTugas.getText().trim();
        if (teks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama tugas tidak boleh kosong!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String prioritas = (String) cbPrioritas.getSelectedItem();
        model.addElement("[" + prioritas + "] " + teks);
        tfTugas.setText("");
        tfTugas.requestFocus();
        lblStatus.setText("Tugas ditambahkan. Total: " + model.size());
    }

    private void hapusTugas() {
        int idx = listTugas.getSelectedIndex();
        if (idx == -1) {
            JOptionPane.showMessageDialog(this, "Pilih tugas yang ingin dihapus dulu.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        model.remove(idx);
        lblStatus.setText("Tugas dihapus. Total: " + model.size());
    }

    private void bersihkanSemua() {
        if (model.isEmpty()) {
            return;
        }
        int pilihan = JOptionPane.showConfirmDialog(this,
                "Hapus semua tugas?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (pilihan == JOptionPane.YES_OPTION) {
            model.clear();
            lblStatus.setText("Semua tugas dihapus.");
        }
    }

    private void toggleSelesai() {
        int idx = listTugas.getSelectedIndex();
        if (idx == -1) {
            return;
        }
        String isi = model.get(idx);
        if (isi.startsWith(TANDA_SELESAI)) {
            model.set(idx, isi.substring(TANDA_SELESAI.length()));
        } else {
            model.set(idx, TANDA_SELESAI + isi);
        }
    }

    // ===== Main =====
    public static void main(String[] args) {
        // GUI Swing sebaiknya dijalankan di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}
