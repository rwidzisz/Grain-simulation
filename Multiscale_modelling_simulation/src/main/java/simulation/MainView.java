package simulation;
import javafx.scene.layout.VBox;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


import java.awt.Color;
import java.awt.Component;

import javax.swing.*;


import java.lang.Math;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainView extends VBox implements ItemListener
{
    JFrame frame;
    JPanel meshControl;
    JPanel meshCanvas;
    JPanel monteCarloCanvas;
    JPanel DRXCanvas;

    JButton runSimulation;

    JTextField seedsAmount;
    JTextField seedsRadius;
    JTextField xDim, yDim;

    JButton[][] element;
    JButton[][] mcElement;
    JButton[][] drxElement;

    JButton generateMesh;
    JButton resetMesh;
    JButton generateSeeds;

    JLabel seedSign1;
    JLabel seedSign2;
    JLabel seedSign3;
    JLabel conditionSign;
    JLabel neighbourSign;
    JLabel canvasSign;
    JLabel iterationSign;
    int iteration=0;


    JComboBox NeighbourBox;
    String NBoxList[]={"Vonneumann","Moore","Hexleft","Hexright","Hexrand","Pentrand"};

    JComboBox SeedBox;
    String SBoxList[]={"Losowe","Rownomierne","Losowe R","Klikanie"};
    String seed_generationType;

    JRadioButton periodic;
    JRadioButton absorbtion;
    ButtonGroup conditions;

    JCheckBox montecarloBox;
    JLabel monrecarloSign;
    JLabel ktSign;
    JTextField ktValue;
    JLabel MCiteraionSign;
    int MCiteration=0;
    JButton runMC;

    JCheckBox DRXBox;
    JLabel DRXSign;
    JLabel BSign;
    JTextField BValue;
    JLabel ASign;
    JTextField AValue;
    JLabel DRXiteraionSign;
    int DRXiteration=0;
    JButton runDRX;
    JButton exportDRX;

    ButtonGroup features;

    double A,B;
    double DRXtime =0;
    double Ro = 1.0;
    double RoElementVal=0.0;
    double sigma = 3.91E+01;

    static final double sigma0 = 0;
    static final double alfa = 1.9;
    static final double mi = 2.57E-10;
    static final double beta = 8E+10;

    List<String[]> dataLines = new ArrayList<>();


    int n,m;
    Mesh mesh1;
    int seeds = 0;
    int numberofSeeds;

    Map<Integer,Integer> ID = new HashMap<Integer,Integer>();
    Map<Integer, Color> IDColors = new HashMap<Integer,Color>();
    ArrayList<String> usedColors = new ArrayList<String>();

    ActionListener actioncreateMesh = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            n=Integer.parseInt(xDim.getText());
            m=Integer.parseInt(yDim.getText());;
            try {
                createMesh(n,m);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            generateMesh.setEnabled(true);
            meshCanvas.setVisible(true);
            resetMesh.setEnabled(true);
            runSimulation.setEnabled(true);

            if(montecarloBox.isSelected())
            {
                monteCarloCanvas.setVisible(true);
                runMC.setEnabled(true);

            }
            if(DRXBox.isSelected())
            {
                DRXCanvas.setVisible(true);
                runDRX.setEnabled(true);

            }

        }
    };

    ActionListener actionclearMesh = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            for(int i=0;i<n;i++)
            {
                for(int j=0;j<m;j++)
                {
                    meshCanvas.removeAll();
                    meshCanvas.revalidate();
                    meshCanvas.repaint();
                    mesh1.mesh[i][j]=0;
                    element[i][j].setBackground(Color.WHITE);
                }
            }
            seeds =0;
            iteration=0;

            if(montecarloBox.isSelected())
            {
                MCiteration=0;
                mesh1.EnergyMap.clear();

                for(int i=0;i<n;i++)
                {
                    for(int j=0;j<m;j++)
                    {
                        monteCarloCanvas.removeAll();
                        monteCarloCanvas.revalidate();
                        monteCarloCanvas.repaint();
                        mcElement[i][j].setBackground(Color.WHITE);
                        runMC.setEnabled(false);
                    }
                }
            }

            if(DRXBox.isSelected())
            {
                DRXiteration=0;
                mesh1.CurrentRecrystalizaton.clear();
                mesh1.PreviousRecrystalizaton.clear();
                mesh1.RoMap.clear();
                DRXtime =0.0;
                Ro=0.0;
                RoElementVal=0.0;
                dataLines.clear();
                runDRX.setEnabled(false);

                for(int i=0;i<n;i++)
                {
                    for(int j=0;j<m;j++)
                    {
                        DRXCanvas.removeAll();
                        DRXCanvas.revalidate();
                        DRXCanvas.repaint();
                        drxElement[i][j].setBackground(Color.WHITE);
                    }
                }
            }



        }
    };

    ActionListener actionComboBox = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == SeedBox) {
                seed_generationType = SeedBox.getSelectedItem().toString();
                if (seed_generationType == "Losowe") {
                    seedsAmount.setEnabled(true);
                    seedsRadius.setEnabled(false);
                    generateSeeds.setEnabled(true);
                } else if (seed_generationType == "Rownomierne") {
                    seedsAmount.setEnabled(true);
                    seedsRadius.setEnabled(false);
                    generateSeeds.setEnabled(true);
                } else if (seed_generationType == "Losowe R") {
                    seedsAmount.setEnabled(true);
                    seedsRadius.setEnabled(true);
                    generateSeeds.setEnabled(true);
                } else if (seed_generationType == "Klikanie") {
                    generateSeeds.setEnabled(false);
                    seedsAmount.setEnabled(false);
                    seedsRadius.setEnabled(false);
                }
            }
        }
    };

    ActionListener actionGenerateSeeds = new ActionListener() {
        int genX =0;
        int genY =0;

        @Override
        public void actionPerformed(ActionEvent e) {

            numberofSeeds = Integer.parseInt(seedsAmount.getText());
            if (numberofSeeds < 0){
                System.err.println("At least one seed!");
                throw new RuntimeException();
            }else if(numberofSeeds > n * m) {
                System.err.println("Too many seeds!");
                throw new RuntimeException();
            }

           if(seed_generationType =="Losowe")
            {
                Random generator = new Random();
                numberofSeeds = Integer.parseInt(seedsAmount.getText());
                int i = 0;

                while(i < numberofSeeds)
                {
                    genX = generator.nextInt(n);
                    genY = generator.nextInt(m);

                    if(mesh1.mesh[genX][genY]==0)
                    {
                        seeds++;
                        mesh1.mesh[genX][genY]= seeds;
                        ID.put(seeds, seeds);
                        mesh1.IDMap.put(seeds, seeds);
                        Color color = generateColor();
                        System.err.println(color);
                        element[genX][genY].setBackground(color);

                        IDColors.put(seeds, color);
                        i++;
                    }
                    else
                    {
                        i=i;
                    }
                }
            }
            else if(seed_generationType =="Losowe R")
            {

                Random generator = new Random();
                numberofSeeds = Integer.parseInt(seedsAmount.getText());

                int radius;
                radius = Integer.parseInt(seedsRadius.getText());

                if(radius<0){
                    System.err.println("Radius can't be negative");
                    throw new RuntimeException();
                }else if(numberofSeeds > n * m) {
                System.err.println("Too many seeds!");
                throw new RuntimeException();
            }

                genX = generator.nextInt(n);
                genY = generator.nextInt(m);

                double distance = 0;
                int[][] tabR = new int[n][2];

                int integer =0;
                while( integer < numberofSeeds)
                {
                    if(mesh1.mesh[genX][genY]==0)
                    {
                        tabR[integer][0]= genX;
                        tabR[integer][1]= genY;
                        seeds++;
                        mesh1.mesh[genX][genY]= seeds;
                        ID.put(seeds, seeds);
                        mesh1.IDMap.put(seeds, seeds);
                        //mesh1.EnergyMap.put(genX+genY,1.0);
                        Color color = generateColor();
                        element[genX][genY].setBackground(color);

                        IDColors.put(seeds, color);
                        integer++;
                    }

                    boolean cond = false;
                    int integer2 =0;
                    while(true)
                    {
                        genX = generator.nextInt(n);
                        genY = generator.nextInt(m);
                        for(int i = 0; i< numberofSeeds; i++)
                        {
                            distance = Math.sqrt(((int)Math.pow(genX -tabR[i][0],2)+(int)Math.pow(genY -tabR[i][1],2)));
                            if(distance <= radius)
                            {
                                cond = true;
                            }
                        }
                        if(cond ==false)
                        {
                            break;
                        }
                        if(integer2 >1000000)
                        {
                            System.err.println("Too many seeds or too high value of radius!");
                            throw new RuntimeException();
                        }
                        cond =false;
                        integer2++;
                    }
                }
            }
            else if(seed_generationType =="Rownomierne")
            {
                numberofSeeds = Integer.parseInt(seedsAmount.getText());
                double nR = Math.sqrt(numberofSeeds);
                int nR2 = (int) nR;
                genX =n/(2* nR2);
                genY =m/(2* nR2);
                for(int i = genX; i<n; i=i+2 * genX)
                {
                    for(int j = genY; j<m; j=j+2 * genY)
                    {
                        seeds++;
                        mesh1.mesh[i][j]= seeds;
                        ID.put(seeds, seeds);
                        mesh1.IDMap.put(seeds, seeds);
                       // mesh1.EnergyMap.put(genX+genY,1.0);
                        Color color = generateColor();
                        element[i][j].setBackground(color);

                        IDColors.put(seeds, color);
                    }
                }
            }
        }
    };

    ActionListener actionrunSimulation= new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(seeds >0){
                iteration++;
                nextIteration();
                iterationSign.setText("Iteracja: "+ iteration);
            }

        }
    };

    ActionListener actionrunMonteCarlo= new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            double checkval;
            checkval=Double.parseDouble(ktValue.getText());

            if(checkval <=0.1 || checkval > 6)
            {
                System.err.println("Value must be beetween:...");
                throw new RuntimeException();
            }

            if(seeds >0){
                MCiteration++;
                nextMCIteration();
                MCiteraionSign.setText("Iteracja: "+ MCiteration);
            }

        }
    };

    ActionListener actionrunDRX= new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            AValue.setText("86710969050178.5");
            BValue.setText("9.41268203527779");
            A=Double.parseDouble(AValue.getText());
            B=Double.parseDouble(BValue.getText());

            mesh1.CriticalRo=(4.21584E+12)/(n*m);
            System.out.println("Ro Krytyczne: " + mesh1.CriticalRo);

            if(seeds >0){
                DRXiteration++;
                nextDRXIteration();
                DRXiteraionSign.setText("Iteracja: "+ DRXiteration);
            }

        }
    };

    ActionListener actionExportToCSV= new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                ExportToCSV();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        }
    };


    MouseListener actionpress = new MouseListener() {

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if(seed_generationType =="Klikanie")
            {
                String command = ((JButton) e.getSource()).getActionCommand();
                Integer pos = Integer.parseInt(command);
                int genX =0, genY =0;
                int counter = 0;
                for(int i = 0;i<n;i++)
                {
                    for(int j=0;j<m;j++)
                    {
                        if(counter == pos)
                        {
                            genX =i;
                            genY =j;
                        }
                        counter++;
                    }
                }
                Object source = e.getSource();
                seeds++;
                ID.put(seeds, seeds);
                mesh1.IDMap.put(seeds, seeds);
                //mesh1.EnergyMap.put(genX+genY,1.0);
                source = e.getSource();
                Color color = generateColor();
                ((Component)source).setBackground(color);

                IDColors.put(seeds, color);
                mesh1.mesh[genX][genY]= seeds;
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }
    };

    void createMesh(int a, int b) throws Exception {
        n=a;
        m=b;
        double sizen =(30.0/n)*20.0;
        double sizem =(30.0/m)*20.0;
        element = new JButton[n][m];


        mesh1 = new Mesh(n,m);
        mesh1.IDMap.put(seeds, seeds);
        //mesh1.EnergyMap.put(0,1.0);
        ID.put(seeds, seeds);
        IDColors.put(seeds,Color.WHITE);

        int pom = 0;

        for( int i=0;i<n;i++)
        {
            for(int j=0;j<m;j++)
            {
                element[i][j] = new JButton();
                element[i][j].setSize((int) sizen,(int) sizem);
                element[i][j].setOpaque(true);
                element[i][j].setBackground(Color.WHITE);
                element[i][j].setLocation((int) sizen *i,(int) sizem *j);
                element[i][j].addMouseListener(actionpress);
                String StringCommand = Integer.toString(pom);
                element[i][j].setActionCommand(StringCommand);
                meshCanvas.add(element[i][j]);
                pom++;
            }
        }

        if(montecarloBox.isSelected())
        {
            frame.setSize(1600,900);
            mcElement =new JButton[n][m];
            //mesh1.EnergyMap.put(0,1.0);

            for( int i=0;i<n;i++)
            {
                for(int j=0;j<m;j++)
                {
                    mcElement[i][j] = new JButton();
                    mcElement[i][j].setSize((int) sizen,(int) sizem);
                    mcElement[i][j].setBackground(Color.WHITE);
                    mcElement[i][j].setLocation((int) sizen *i,(int) sizem *j);
                    monteCarloCanvas.add(mcElement[i][j]);
                    pom++;
                }
            }


            monteCarloCanvas.setVisible(false);
            frame.add(monteCarloCanvas);
        }

        if(DRXBox.isSelected())
        {
            frame.setSize(1600,900);
            drxElement =new JButton[n][m];

            for( int i=0;i<n;i++)
            {
                for(int j=0;j<m;j++)
                {
                    drxElement[i][j] = new JButton();
                    drxElement[i][j].setSize((int) sizen,(int) sizem);
                    drxElement[i][j].setBackground(Color.WHITE);
                    drxElement[i][j].setLocation((int) sizen *i,(int) sizem *j);
                    DRXCanvas.add(drxElement[i][j]);
                    pom++;
                }
            }


            DRXCanvas.setVisible(false);
            frame.add(DRXCanvas);
        }


        meshCanvas.setVisible(false);
        frame.add(meshCanvas);
    }

    Color generateColor()
    {
        int R=0,G=0,B=0;
        String color;
        Color color1;
        while(true){
            Random generator = new Random();
            R = generator.nextInt(254);
            G = generator.nextInt(254);
            B = generator.nextInt(254);

            String strR=Integer.toString(R);
            String strG=Integer.toString(G);
            String strB=Integer.toString(B);

            color=strR+strG+strB;
            if(!usedColors.contains(color)){
                usedColors.add(color);
                break;
            }
        }
        color1 = new Color(R, G, B);
        return color1;
    }

    Color generateColorDRX()
    {
        int R=0,G=0,B=0;
        Color color1;

        Random generator = new Random();
        R = generator.nextInt(134);
        R=R+120;

        color1 = new Color(R, G, B);
        return color1;
    }

    void nextIteration()//Iteracja dla Symulacji CA
    {
        boolean periodic = true;
        String neighbourhood = NeighbourBox.getSelectedItem().toString();


        if(this.periodic.isSelected())
        {
            periodic = true;
        }
        else if(absorbtion.isSelected())
        {
            periodic = false;
        }
        switch (neighbourhood){
            case "Moore":
                mesh1.Iteration(periodic,"Moore");
                break;
            case "Vonneumann":
                mesh1.Iteration(periodic,"Vonneumann");
                break;
            case "Hexleft":
                mesh1.Iteration(periodic,"Hexleft");
                break;
            case "Hexright":
                mesh1.Iteration(periodic,"Hexright");
                break;
            case "Hexrand":
                mesh1.Iteration(periodic,"Hexrand");
                break;
            case "Pentrand":
                mesh1.Iteration(periodic,"Pentrand");
                break;
        }

        for(int i=0;i<n;i++)
        {
            for(int j=0;j<m;j++)
            {
                element[i][j].setBackground(IDColors.get(ID.get(mesh1.mesh[i][j])));

            }
        }

    }

    void nextMCIteration()
    {
        boolean periodic = true;
        double kt = 0;
        kt=Double.parseDouble(ktValue.getText());
        String neighbourhood = NeighbourBox.getSelectedItem().toString();

        if(this.periodic.isSelected())
        {
            periodic = true;
        }
        else if(absorbtion.isSelected())
        {
            periodic = false;
        }

        if(MCiteration==1)
        {
            for(int i=0;i<n;i++) {
                for (int j = 0; j < m; j++) {
                    mesh1.EnergyMap.put(Integer.toString(i)+"|"+Integer.toString(j),1.0);
                }
            }
            mesh1.calculateMeshEnergy(periodic);
        }

        mesh1.MCIteration(periodic, kt);
        mesh1.calculateMeshEnergy(periodic);
        
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<m;j++)
            {

                element[i][j].setBackground(IDColors.get(ID.get(mesh1.mesh[i][j])));
                double energy=mesh1.EnergyMap.get(Integer.toString(i)+"|"+Integer.toString(j));
                switch ((int) energy)
                {
                    case 1:
                        Color color1 = new Color(255, 255, 255);
                        mcElement[i][j].setBackground(color1);
                        break;
                    case 2:
                        Color color2 = new Color(57, 217, 169);
                        mcElement[i][j].setBackground(color2);
                        break;
                    case 3:
                        Color color3 = new Color(5, 203, 145);
                        mcElement[i][j].setBackground(color3);
                        break;
                    case 4:
                        Color color4 = new Color(14, 206, 224);
                        mcElement[i][j].setBackground(color4);
                        break;
                    case 5:
                        Color color5 = new Color(34, 137, 215);
                        mcElement[i][j].setBackground(color5);
                        break;
                    case 6:
                        Color color6 = new Color(23, 82, 184);
                        mcElement[i][j].setBackground(color6);
                        break;
                    case 7:
                        Color color7 = new Color(5, 29, 177);
                        mcElement[i][j].setBackground(color7);
                        break;
                    case 8:
                        Color color8 = new Color(7, 22, 83);
                        mcElement[i][j].setBackground(color8);
                        break;
                }

            }
        }

    }

    void nextDRXIteration() //Iteracja dla Dyslokacji
    {
        DRXtime +=0.001;
        double currentRo=CalculateRo();
        double deltaRo=currentRo-Ro;
        double nextRo=0.0;

        boolean periodic = true;

        if(this.periodic.isSelected())
        {
            periodic = true;
        }
        else if(absorbtion.isSelected())
        {
            periodic = false;
        }


        if(DRXiteration==1)
        {
            dataLines.add(new String[]
                    { "Time",  "Ro", "Sigma", "Suma_gęstośći"});
            dataLines.add(new String[]
                    { String.valueOf(DRXtime), String.valueOf(Ro), String.valueOf(sigma), "0"});

            for(int i=0;i<n;i++) {
                for (int j = 0; j < m; j++) {
                    mesh1.RoMap.put(Integer.toString(i)+"|"+Integer.toString(j),RoElementVal);
                    mesh1.PreviousRecrystalizaton.put(Integer.toString(i)+"|"+Integer.toString(j),false);
                    mesh1.CurrentRecrystalizaton.put(Integer.toString(i)+"|"+Integer.toString(j),false);
                    drxElement[i][j].setBackground(IDColors.get(ID.get(mesh1.mesh[i][j]))); //Wypełniamy też siatkę

                }
            }
        }
        double pom=0.0;
        RoElementVal+=deltaRo/(n*m);
        for(int i=0;i<n;i++) {
            for (int j = 0; j < m; j++) {
                pom=mesh1.RoMap.get(Integer.toString(i) +"|"+ Integer.toString(j));
                pom+=deltaRo/(n*m);
                mesh1.RoMap.replace(Integer.toString(i) +"|"+ Integer.toString(j), pom);
            }
        }

        dataLines.add(new String[]
                { String.valueOf(DRXtime), String.valueOf(Ro), String.valueOf(sigma),String.valueOf(deltaRo)});

        mesh1.DRXIteration(periodic);

        for(int i=0;i<n;i++) {
            for (int j = 0; j < m; j++) {
                if(mesh1.CurrentRecrystalizaton.get(Integer.toString(i)+"|"+Integer.toString(j))==true)
                {
                    drxElement[i][j].setBackground(generateColorDRX());
                    mesh1.RoMap.replace(Integer.toString(i)+"|"+Integer.toString(j),0.0);
                    seeds++;
                    mesh1.mesh[i][j]=seeds;
                    ID.put(seeds,seeds);
                    mesh1.IDMap.put(seeds,seeds);
                }



            }
        }

        nextRo+=RoElementVal;
        Ro=nextRo;

        mesh1.PreviousRecrystalizaton=mesh1.CurrentRecrystalizaton;
        for(int i=0;i<n;i++) {
            for (int j = 0; j < m; j++) {
                mesh1.CurrentRecrystalizaton.replace(Integer.toString(i)+"|"+Integer.toString(j),false);
            }
        }


    }

    public double CalculateRo(){
        return A/B+(1-A/B)*Math.exp(-B* DRXtime);
    }

    public double CalculateSigma(){
        return sigma0 + alfa*beta*mi*Math.sqrt(Ro);
    }

    MainView ()
    {
        frame = new JFrame("Symulacja rozrostu ziaren");
        canvasSign= new JLabel("Podaj rozmiary przestrzeni:");
        meshControl = new JPanel();
        meshCanvas = new JPanel();
        seedsAmount = new JTextField();
        seedsRadius = new JTextField();
        xDim = new JTextField();
        yDim = new JTextField();
        seedSign1 = new JLabel("Promien:");
        seedSign2 = new JLabel("Ilosc ziaren:");
        seedSign3 = new JLabel("Typ generacji:");
        generateMesh = new JButton("Wygeneruj Przestrzen");
        resetMesh = new JButton("Resetuj Przestrzen");
        generateSeeds = new JButton("Generuj ziarna");
        neighbourSign=new JLabel("Sąsiedztwo:");
        conditionSign = new JLabel("Warunek:");
        periodic = new JRadioButton("Periodyczne");
        absorbtion = new JRadioButton("Absorbujace");
        runSimulation=new JButton("Uruchom symulacje");
        iterationSign=new JLabel("Iteracja: 0");
        montecarloBox=new JCheckBox();
        monrecarloSign=new JLabel("Monte Carlo:");
        monteCarloCanvas =new JPanel();
        ktSign=new JLabel("kt value:");
        ktValue= new JTextField();

        MCiteraionSign = new JLabel("Iteracja: 0");
        runMC = new JButton("Uruchom algorytm");

        DRXCanvas=new JPanel();
        DRXBox=new JCheckBox();
        DRXSign= new JLabel("Rekrystalizacja:");
        BSign=new JLabel("B:");
        BValue=new JTextField();
        ASign=new JLabel("A:");
        AValue=new JTextField();
        DRXiteraionSign=new JLabel("Iteracja: 0");
        runDRX=new JButton("Rekrystalizacja");
        exportDRX=new JButton("Eksportuj");

        seeds = 0;

        canvasSign.setSize(200,20);
        canvasSign.setLocation(10,1);
        xDim.setSize(60,20);
        xDim.setLocation(10, 20);
        yDim.setSize(60,20);
        yDim.setLocation(70, 20);
        generateMesh.setSize(150, 40);
        generateMesh.setLocation(10, 50);
        generateMesh.addActionListener(actioncreateMesh);
        generateMesh.setOpaque(true);
        generateMesh.setContentAreaFilled(true);
        resetMesh.setSize(150, 30);
        resetMesh.setLocation(10, 90);
        resetMesh.addActionListener(actionclearMesh);
        resetMesh.setEnabled(false);

        seedSign1.setSize(80,20);
        seedSign1.setLocation(10, 150);
        seedSign2.setSize(80, 20);
        seedSign2.setLocation(10, 130);

        seedsAmount.setSize(60, 20);
        seedsAmount.setLocation(100, 130);
        seedsAmount.setEnabled(false);
        seedsRadius.setSize(60, 20);
        seedsRadius.setLocation(100, 150);
        seedsRadius.setEnabled(false);
        generateSeeds.setSize(150,30);
        generateSeeds.setLocation(10,180);
        generateSeeds.addActionListener(actionGenerateSeeds);
        generateSeeds.setEnabled(false);

        seedSign3.setSize(80, 20);
        seedSign3.setLocation(10, 220);
        SeedBox = new JComboBox(SBoxList);
        SeedBox.setSize(100,20);
        SeedBox.setLocation(10,240);
        SeedBox.addActionListener(actionComboBox);

        neighbourSign.setSize(100,20);
        neighbourSign.setLocation(10, 280);
        NeighbourBox = new JComboBox(NBoxList);
        NeighbourBox.setSize(100,20);
        NeighbourBox.setLocation(10,300);

        conditions = new ButtonGroup();
        conditionSign.setSize(100,20);
        conditionSign.setLocation(10, 340);
        periodic.setSize(120, 20);
        periodic.setLocation(10, 360);
        periodic.doClick();
        absorbtion.setSize(120,20);
        absorbtion.setLocation(10, 380);
        conditions.add(periodic);
        conditions.add(absorbtion);

        runSimulation.setSize(150,50);
        runSimulation.setLocation(10,420);
        runSimulation.setEnabled(true);
        runSimulation.addActionListener(actionrunSimulation);
        iterationSign.setSize(150,30);
        iterationSign.setLocation(10,470);

        monrecarloSign.setSize(80,20);
        monrecarloSign.setLocation(10,510);
        montecarloBox.setSize(20,20);
        montecarloBox.setLocation(85,510);

        ktSign.setSize(80,20);
        ktSign.setLocation(10,530);
        ktValue.setSize(30,20);
        ktValue.setLocation(60,530);

        MCiteraionSign.setSize(150,30);
        MCiteraionSign.setLocation(10,610);

        runMC.setSize(150,50);
        runMC.setLocation(10,560);
        runMC.setEnabled(false);
        runMC.addActionListener(actionrunMonteCarlo);


        DRXSign.setSize(80,20);
        DRXSign.setLocation(10,650);
        DRXBox.setSize(20,20);
        DRXBox.setLocation(85,650);

        ASign.setSize(80,20);
        ASign.setLocation(10,670);
        AValue.setSize(110,20);
        AValue.setLocation(30,670);
        BSign.setSize(80,20);
        BSign.setLocation(10,690);
        BValue.setSize(110,20);
        BValue.setLocation(30,690);

        features=new ButtonGroup();
        features.add(montecarloBox);
        features.add(DRXBox);

        DRXiteraionSign.setSize(150,20);
        DRXiteraionSign.setLocation(10,770);

        runDRX.setSize(150,50);
        runDRX.setLocation(10,720);
        runDRX.setEnabled(false);
        runDRX.addActionListener(actionrunDRX);

        exportDRX.setSize(120,30);
        exportDRX.setLocation(10,810);
        exportDRX.setEnabled(true);
        exportDRX.addActionListener(actionExportToCSV);



        meshControl.add(canvasSign);
        meshControl.add(xDim);
        meshControl.add(yDim);
        meshControl.add(generateMesh);
        meshControl.add(resetMesh);
        meshControl.add(seedsAmount);
        meshControl.add(seedsRadius);
        meshControl.add(generateSeeds);

        meshControl.add(seedSign1);
        meshControl.add(seedSign2);
        meshControl.add(seedSign3);
        meshControl.add(neighbourSign);
        meshControl.add(conditionSign);

        meshControl.add(SeedBox);
        meshControl.add(NeighbourBox);
        meshControl.add(periodic);
        meshControl.add(absorbtion);

        meshControl.add(runSimulation);
        meshControl.add(iterationSign);

        meshControl.add(monrecarloSign);
        meshControl.add(montecarloBox);
        meshControl.add(ktSign);
        meshControl.add(ktValue);
        meshControl.add(runMC);
        meshControl.add(MCiteraionSign);

        meshControl.add(DRXBox);
        meshControl.add(DRXSign);
        meshControl.add(BSign);
        meshControl.add(BValue);
        meshControl.add(ASign);
        meshControl.add(AValue);

        meshControl.add(runDRX);
        meshControl.add(DRXiteraionSign);
        meshControl.add(exportDRX);


        meshControl.setLayout(null);
        meshControl.setSize(200,1000);
        meshControl.setLocation(0, 0);
        meshCanvas.setLayout(null);
        meshCanvas.setSize(800,800);
        meshCanvas.setLocation(250, 50);

        monteCarloCanvas.setLayout(null);
        monteCarloCanvas.setSize(800,800);
        monteCarloCanvas.setLocation(900, 50);

        DRXCanvas.setLayout(null);
        DRXCanvas.setSize(800,800);
        DRXCanvas.setLocation(900, 50);

        frame.setLayout(null);
        frame.getContentPane().add(meshControl);
        frame.setSize(950, 900);
        frame.setSize(950, 900);
        frame.setLocation(300, 50);
        frame.setVisible(true);
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public void ExportToCSV() throws IOException {
        File csvOutputFile = new File("output.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }
        System.out.println("Exported to csv");
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }


    @Override
    public void itemStateChanged(ItemEvent e) {

    }
}



