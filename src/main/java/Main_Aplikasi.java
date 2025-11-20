import login_utils.*;

import java.util.Scanner;

import login_utils.Mahasiswa;
import login_utils.Matkul;

// import login_utils.Mahasiswa;
// import login_utils.Matkul;
// import login_utils.menu_mhs;


public class Main_Aplikasi
{
    public static void main(String[] args){
        Matkul main_matkul= new Matkul();
        Mahasiswa mhs1 = new Mahasiswa("EMIR","241511007");;
        Scanner input_choice = new Scanner(System.in);

        main_matkul.create_matkul_full_info("indo","T","senin",9,20,11,40);
        mhs1.set_index_matakuliah(main_matkul.search_matkul("indo"));
        mhs1.put_status_matakuliah_false(main_matkul.search_matkul("indo"));
        
        main_matkul.create_matkul_full_info("prakarya","T","senin",9,20,10,40);
        mhs1.set_index_matakuliah(main_matkul.search_matkul("prakarya"));
        mhs1.put_status_matakuliah_false(main_matkul.search_matkul("prakarya"));
        
        
        main_matkul.create_matkul_full_info("ptik","P","senin",7,20,8,40);
        mhs1.set_index_matakuliah(main_matkul.search_matkul("ptik"));
        mhs1.put_status_matakuliah_false(main_matkul.search_matkul("ptik"));
        
        main_matkul.create_matkul_full_info("seni_budaya","T","selasa",15,20,19,40);
        mhs1.set_index_matakuliah(main_matkul.search_matkul("seni_budaya"));
        mhs1.put_status_matakuliah_false(main_matkul.search_matkul("seni_budaya"));

        int choice = 0;
        while(true){
            menu_mhs.menu();
            choice=  input_choice.nextInt();
            switch (choice){  
                case 1:
                    //print mata kuliah
                    main_matkul.print_kuliah_yang_bisa_absen(9,40,mhs1);
                    break;
            
                case 2:
                    System.out.println("absen sedang di proses ...");
                    main_matkul.absen(9, 40, mhs1);
                    break;

                // case 3:
                // exit();

                default:
                    break;
            }
        }
    }
}