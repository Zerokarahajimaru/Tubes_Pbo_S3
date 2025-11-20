import login_utils.*;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;




import static org.mockito.Mockito.*;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class Akademik_Test {


private Matkul test_data_matkul1;
private Mahasiswa test_data_mahasiswa1;
 
    @BeforeEach
    public void setting_object_matkul(){
        test_data_matkul1 = new Matkul();
        test_data_matkul1.create_matkul_full_info("sbk","TEORI","SENIN",5,40,10,50);
    }

    @BeforeEach
    public void setting_object_mahasiswa(){
        test_data_mahasiswa1 = new Mahasiswa("yukanna","290187211");
    }








    @Test
    @DisplayName("case 1")
    public void test_METHOD_print_kuliah_KETIKA_ada_matakuliah_yang_bisa_absen(){
        int start_hour = 9;
        int start_menit =20;
        int end_hour = 11;
        int end_menit = 10;

        PrintStream originalOut = System.out;

        //berguna untuk mengalihkan output jadi ke objek bukan ke termminal
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Matkul mock_matkul= new Matkul();
        Mahasiswa mock_mahasiswa = new Mahasiswa("Budi","241511007");

        mock_mahasiswa.set_status_matakuliah(false);
        
        mock_matkul.create_matkul_full_info("prakarya","teori","senninn",start_hour,start_menit,end_hour,end_menit);

        mock_matkul.print_kuliah_yang_bisa_absen(10,20,mock_mahasiswa);
        
        String output = outContent.toString().trim();
        System.setOut(originalOut);
                               
        assertEquals("prakarya    : teori       :  true [matkul bisa absen :)]",output);
    } 



    @Test
    @DisplayName("case 2")
    public void test_METHOD_print_kuliah_KETIKA_tidak_ada_matakuliah_yang_aktif(){
        int start_hour = 9;
        int start_menit =20;
        int end_hour = 11;
        int end_menit = 10;

        PrintStream originalOut = System.out;

        //berguna untuk mengalihkan output jadi ke objek bukan ke termminal
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Matkul mock_matkul= new Matkul();
        Mahasiswa mock_mahasiswa = new Mahasiswa("Budi","241511007");

        mock_mahasiswa.set_status_matakuliah(true);
        
        mock_matkul.create_matkul_full_info("prakarya","teori","senninn",start_hour,start_menit,end_hour,end_menit);

        mock_matkul.print_kuliah_yang_bisa_absen(7,25,mock_mahasiswa);
        
        String output = outContent.toString().trim();
        System.setOut(originalOut);
                               
        assertEquals("tidak ada matakuliah saat ini tuan",output);
    }

    @Test
    @DisplayName("case 3")
    public void test_METHOD_print_kuliah_KETIKA_mahasiswa_belum_mengambil_matkul(){
        int start_hour = 9;
        int start_menit =20;
        int end_hour = 11;
        int end_menit = 10;

        PrintStream originalOut = System.out;

        //berguna untuk mengalihkan output jadi ke objek bukan ke termminal
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Matkul mock_matkul= new Matkul();
        Mahasiswa mock_mahasiswa = new Mahasiswa("Budi","241511007");

        mock_mahasiswa.set_status_matakuliah(true);
        
        mock_matkul.print_kuliah_yang_bisa_absen(7,25,mock_mahasiswa);
        
        String output = outContent.toString().trim();
        System.setOut(originalOut);
                               
        assertEquals("anda belum memmilih matakuliah",output);
    } 

    // test absen ketika mahasiswa sudah  mengambil matkul dan sedang matkul sedang berlangsung
    public void test_absen_ketika_mahasiswa_sudah_absen()
    {
        PrintStream originalOut = System.out;

        //berguna untuk mengalihkan output jadi ke objek bukan ke termminal
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        test_data_matkul1.absen(8,40 ,test_data_mahasiswa1);
        
        String output = outContent.toString().trim();
        System.setOut(originalOut);
        
        assertAll(
            assertEquals("absen sukses",output),
            assertEquals(2, );


        );

    }

    
} 
