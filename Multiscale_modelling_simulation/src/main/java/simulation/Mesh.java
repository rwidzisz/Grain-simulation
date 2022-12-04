package simulation;


import java.util.*;
import java.util.List;

public class Mesh {
    private int n, m;
    public int[][] mesh;
    Map<Integer,Integer> IDMap = new HashMap<Integer,Integer>();
    int[][] neighbours = new int[3][3];

    Map<String,Double> EnergyMap = new HashMap<String, Double>();
    int MCRDXneighbourhood;

    Map<String,Double> RoMap = new HashMap<String, Double>();
    Map<String,Boolean> PreviousRecrystalizaton = new HashMap<String, Boolean>();
    Map<String,Boolean> CurrentRecrystalizaton = new HashMap<String, Boolean>();
    double CriticalRo=1;

    boolean[][] neighboursRe = new boolean[3][3];
    double[][] neighboursDys = new double[3][3];


    Mesh(int n, int m) throws Exception
    {
        if (n < 0 || m < 0 || (n * m) < 9) {
            throw new Exception("Mesh should be a least 3x3");
        }
        this.n = n;
        this.m = m;
        this.mesh = new int[this.n][this.m];
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<m;j++)
            {
                this.mesh[i][j]=0;
            }
        }
    }

    //Neigbhours functions & transition rules
    public int neighboursID(int[][] tab)
    {
        int returnval;
        ArrayList<Integer> IDS = new ArrayList<Integer>();
        IDS.clear();


        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++)
            {
                //System.out.println(tab[i][j]);
                if(i!=1 || j!=1)
                {
                    for(int k = 0; k< IDMap.size(); k++)
                    {
                        if(tab[i][j]== IDMap.get(k)){
                            if(k!=0)
                                IDS.add(k);
                        }
                    }
                }
            }
        }

        if(IDS.size()==0)
        {
            returnval=0;
        }else{
            returnval=mostCommon(IDS);
        }

        return returnval;
    }

    public static <T> T mostCommon(List<T> list) {
        Map<T, Integer> map = new HashMap<>();

        for (T t : list) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Map.Entry<T, Integer> max = null;

        for (Map.Entry<T, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }

        return max.getKey();
    }

    public int Vonneumann(int x, int y, boolean periodic)
    {
        if(periodic){
            neighbours = period(x,y);
        }else{
            neighbours = absorption(x, y);
        }

        neighbours[0][0]=0;
        neighbours[0][2]=0;
        neighbours[2][0]=0;
        neighbours[2][2]=0;
        MCRDXneighbourhood =2;

        int nID = neighboursID(neighbours);
        return nID;
    }

    public int Moore(int x, int y, boolean periodic)
    {
        if(periodic){
            neighbours = period(x,y);
        }else{
            neighbours = absorption(x,y);
        }
        MCRDXneighbourhood =1;

        int nID = neighboursID(neighbours);
        return nID;

    }

    public int Hexagonalright(int x, int y, boolean periodic)
    {
        if(periodic){
            neighbours = period(x,y);
        }else{
            neighbours = absorption(x, y);
        }

        neighbours[0][0]=0;
        neighbours[2][2]=0;
        MCRDXneighbourhood =3;

        int nID = neighboursID(neighbours);
        return nID;
    }

    public int Haxagonalleft(int x, int y, boolean periodic)
    {
        if(periodic){
            neighbours = period(x,y);
        }else{
            neighbours = absorption(x, y);
        }

        neighbours[0][2]=0;
        neighbours[2][0]=0;
        MCRDXneighbourhood =4;

        int nID = neighboursID(neighbours);
        return nID;
    }

    public int Hexagonalrandom(int x, int y, boolean periodic)
    {
        Random generator = new Random();

        if(periodic){
            neighbours = period(x,y);
        }else{
            neighbours = absorption(x, y);
        }

        int rand = generator.nextInt(2);
        switch (rand){
            case 0:
                neighbours[0][2]=0;
                neighbours[2][0]=0;
                MCRDXneighbourhood =4;
                break;
            case 1:
                neighbours[0][0]=0;
                neighbours[2][2]=0;
                MCRDXneighbourhood =3;
                break;
        }

        int nID = neighboursID(neighbours);
        return nID;

    }

    public int Pentagonalrandom(int x, int y, boolean periodic)
    {
        Random generator = new Random();

        if(periodic){
            neighbours = period(x,y);
        }else{
            neighbours = absorption(x, y);
        }

        int rand = generator.nextInt(4);

        switch (rand){
            case 0:
                neighbours[0][0]=0;
                neighbours[1][0]=0;
                neighbours[2][0]=0;
                MCRDXneighbourhood =5;
                break;
            case 1:
                neighbours[0][2]=0;
                neighbours[1][2]=0;
                neighbours[2][2]=0;
                MCRDXneighbourhood =6;
                break;
            case 2:
                neighbours[0][2]=0;
                neighbours[0][1]=0;
                neighbours[0][2]=0;
                MCRDXneighbourhood =7;
                break;
            case 3:
                neighbours[2][0]=0;
                neighbours[2][1]=0;
                neighbours[2][2]=0;
                MCRDXneighbourhood =8;
                break;
        }

        int nID = neighboursID(neighbours);
        return nID;
    }

    //Iteration for CA
    public void Iteration(boolean b, String type)
    {
        int[][] pomMesh = new int[this.n][this.m];
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<m;j++)
            {
                if(this.mesh[i][j]==0)
                {
                    switch (type){
                        case "Moore":
                            pomMesh[i][j] = Moore(i,j,b);
                            break;
                        case "Vonneumann":
                            pomMesh[i][j] = Vonneumann(i,j,b);
                            break;
                        case "Hexleft":
                            pomMesh[i][j] = Haxagonalleft(i,j,b);
                            break;
                        case "Hexright":
                            pomMesh[i][j] = Hexagonalright(i,j,b);
                            break;
                        case "Hexrand":
                            pomMesh[i][j] = Hexagonalrandom(i,j,b);
                            break;
                        case "Pentrand":
                            pomMesh[i][j] = Pentagonalrandom(i,j,b);
                            break;
                    }
                }
                else
                {
                    pomMesh[i][j] = this.mesh[i][j];
                }

            }
        }
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<m;j++)
            {
                this.mesh[i][j] = pomMesh[i][j];

            }
        }
    }

    //Monte Carlo Methods
    public void calculateMeshEnergy(boolean b) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                calculateElementEnergry(i,j,b);
            }
        }
    }

    public void calculateElementEnergry(int x,int y, boolean periodic)
    {
        if(periodic){
            neighbours = period(x,y);
        }else{
            neighbours = absorption(x, y);
        }
        switch (MCRDXneighbourhood){
            case 1:
                break;
            case 2:
                neighbours[0][0]=0;
                neighbours[0][2]=0;
                neighbours[2][0]=0;
                neighbours[2][2]=0;
                break;
            case 3:
                neighbours[0][0]=0;
                neighbours[2][2]=0;
                break;
            case 4:
                neighbours[0][2]=0;
                neighbours[2][0]=0;
                break;
            case 5:
                neighbours[0][0]=0;
                neighbours[1][0]=0;
                neighbours[2][0]=0;
                break;
            case 6:
                neighbours[0][2]=0;
                neighbours[1][2]=0;
                neighbours[2][2]=0;
                break;
            case 7:
                neighbours[0][2]=0;
                neighbours[0][1]=0;
                neighbours[0][2]=0;
                break;
            case 8:
                neighbours[2][0]=0;
                neighbours[2][1]=0;
                neighbours[2][2]=0;
                break;
        }

        double energy=1.0;
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++)
            {
                if(i!=1 || j!=1)
                {
                    if(neighbours[i][j]!=neighbours[1][1] && neighbours[i][j]!=0)
                    {
                        energy++;
                    }
                }

            }
        }
        this.EnergyMap.replace(Integer.toString(x)+"|"+Integer.toString(y),energy);
    }

    public double calculateseedEnergy(int[][] tab)
    {
        double energy=0.0;
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++)
            {
                if(i!=1 || j!=1)
                {
                    if(tab[i][j]!=tab[1][1] && tab[i][j]!=0)
                    {
                        energy++;
                    }
                }

            }
        }

        return energy;
    }

    public int MonteCarlo(int x, int y, boolean periodic, double kt){ //8casów colorów
        if(periodic){
            neighbours = period(x,y);
        }else{
            neighbours = absorption(x, y);
        }
        switch (MCRDXneighbourhood){
            case 1:
                break;
            case 2:
                neighbours[0][0]=0;
                neighbours[0][2]=0;
                neighbours[2][0]=0;
                neighbours[2][2]=0;
                break;
            case 3:
                neighbours[0][0]=0;
                neighbours[2][2]=0;
                break;
            case 4:
                neighbours[0][2]=0;
                neighbours[2][0]=0;
                break;
            case 5:
                neighbours[0][0]=0;
                neighbours[1][0]=0;
                neighbours[2][0]=0;
                break;
            case 6:
                neighbours[0][2]=0;
                neighbours[1][2]=0;
                neighbours[2][2]=0;
                break;
            case 7:
                neighbours[0][2]=0;
                neighbours[0][1]=0;
                neighbours[0][2]=0;
                break;
            case 8:
                neighbours[2][0]=0;
                neighbours[2][1]=0;
                neighbours[2][2]=0;
                break;
        }

        int rx=0,ry=0;
        int initialID=neighbours[1][1];

        //losowanie i przyjmowanie ID
        while(true)
        {
            Random generator = new Random();
            int rand = generator.nextInt(8);
            switch(rand)
            {
                case 0:
                    rx=0;
                    ry=0;
                    break;
                case 1:
                    rx=0;
                    ry=1;
                    break;
                case 2:
                    rx=0;
                    ry=2;
                    break;
                case 3:
                    rx=1;
                    ry=0;
                    break;
                case 4:
                    rx=1;
                    ry=2;
                    break;
                case 5:
                    rx=2;
                    ry=0;
                    break;
                case 6:
                    rx=2;
                    ry=1;
                    break;
                case 7:
                    rx=2;
                    ry=2;
                    break;
            }
            if(neighbours[rx][ry]!=0)
            {
                neighbours[1][1]=neighbours[rx][ry];
                break;
            }

        }

        int deltaE= (int) ((int) (calculateseedEnergy(neighbours))-this.EnergyMap.get(Integer.toString(x)+"|"+Integer.toString(y)));
        Random generator = new Random();

        double propability=generator.nextDouble();
        if(deltaE<=0 || propability <= Math.exp(-deltaE/kt)){
            initialID=neighbours[rx][ry];   //Podmiana ID
        }

        return initialID;
    }

    //Iteration for Monte Carlo
    public void MCIteration(boolean b, double kt)
    {
        int[][] pomMesh = new int[this.n][this.m];

        List<int[]> Coordinates = new ArrayList<int[]>();
        Coordinates.clear();
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<m;j++)
            {
                int cords[] = {i,j};
                Coordinates.add(cords);
            }
        }

        Collections.shuffle(Coordinates);

        for(int iterator=0;iterator<Coordinates.size();iterator++)
        {
            int x=Coordinates.get(iterator)[0];
            int y=Coordinates.get(iterator)[1];

            pomMesh[x][y]=MonteCarlo(x,y,b,kt);
            this.mesh[x][y] = pomMesh[x][y];
     }
    }

    //Recrystallization Methods
    public boolean checkBorder(int[][] tab)
    {
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++)
            {
                if(i!=1 || j!=1)
                {
                    if(tab[i][j]!=tab[1][1] && tab[i][j]!=0)
                    {
                        return true;

                    }
                }

            }
        }

        return false;
    }

    public boolean checkRecrystalization(boolean[][] tab)
    {
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++)
            {
                if(i!=1 || j!=1)
                {
                    if(tab[i][j]==true)
                    {
                        return true;
                    }
                }

            }
        }

        return false;
    }

    public boolean checkDislocationValue(double[][] tab)
    {
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++)
            {
                if(i!=1 || j!=1)
                {
                    if(tab[i][j]>tab[1][1])
                    {
                        return false;
                    }
                }

            }
        }

        return true;
    }




    public int Recrystallization(int x, int y, boolean periodic){
        if(periodic){
            neighboursRe = periodRecrystalization(x,y);
            neighboursDys= periodDyslocation(x,y);
            neighbours = period(x,y);
        }else{
            neighboursRe = absorptionRecrystalization(x,y);
            neighboursDys= absorbtionDyslocation(x,y);
            neighbours=absorption(x,y);
        }
        switch (MCRDXneighbourhood){
            case 1:
                break;
            case 2:
                neighbours[0][0]=0;
                neighbours[0][2]=0;
                neighbours[2][0]=0;
                neighbours[2][2]=0;

                neighboursRe[0][0]=false;
                neighboursRe[0][2]=false;
                neighboursRe[2][0]=false;
                neighboursRe[2][2]=false;

                neighboursDys[0][0]=0.0;
                neighboursDys[0][2]=0.0;
                neighboursDys[2][0]=0.0;
                neighboursDys[2][2]=0.0;
                break;
            case 3:
                neighbours[0][0]=0;
                neighbours[2][2]=0;

                neighboursRe[0][0]=false;
                neighboursRe[2][2]=false;

                neighboursDys[0][0]=0.0;
                neighboursDys[2][2]=0.0;
                break;
            case 4:
                neighbours[0][2]=0;
                neighbours[2][0]=0;

                neighboursRe[0][2]=false;
                neighboursRe[2][0]=false;

                neighboursDys[0][2]=0.0;
                neighboursDys[2][0]=0.0;
                break;
            case 5:
                neighbours[0][0]=0;
                neighbours[1][0]=0;
                neighbours[2][0]=0;

                neighboursRe[0][0]=false;
                neighboursRe[1][0]=false;
                neighboursRe[2][0]=false;

                neighboursDys[0][0]=0.0;
                neighboursDys[1][0]=0.0;
                neighboursDys[2][0]=0.0;
                break;
            case 6:
                neighbours[0][2]=0;
                neighbours[1][2]=0;
                neighbours[2][2]=0;

                neighboursRe[0][2]=false;
                neighboursRe[1][2]=false;
                neighboursRe[2][2]=false;

                neighboursDys[0][2]=0.0;
                neighboursDys[1][2]=0.0;
                neighboursDys[2][2]=0.0;
                break;
            case 7:
                neighbours[0][2]=0;
                neighbours[0][1]=0;
                neighbours[0][2]=0;

                neighboursRe[0][2]=false;
                neighboursRe[0][1]=false;
                neighboursRe[0][2]=false;

                neighboursDys[0][2]=0.0;
                neighboursDys[0][1]=0.0;
                neighboursDys[0][2]=0.0;
                break;
            case 8:
                neighbours[2][0]=0;
                neighbours[2][1]=0;
                neighbours[2][2]=0;

                neighboursRe[2][0]=false;
                neighboursRe[2][1]=false;
                neighboursRe[2][2]=false;

                neighboursDys[2][0]=0.0;
                neighboursDys[2][1]=0.0;
                neighboursDys[2][2]=0.0;
                break;
        }

        if(checkRecrystalization(neighboursRe)==true && checkDislocationValue(neighboursDys)==true)
        {
            this.CurrentRecrystalizaton.replace(Integer.toString(x)+Integer.toString(y),true);
        }

        if(checkBorder(neighbours)==true && neighboursDys[1][1]>this.CriticalRo)
        {
             this.CurrentRecrystalizaton.replace(Integer.toString(x)+"|"+Integer.toString(y),true);
        }
        return 0;
    }



    //Iteraion for DRX
    public void DRXIteration(boolean b) //Ulpesz two dimensonal arrays tak żeby działały C:
    {
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<m;j++) {
                Recrystallization(i,j,b);
            }

        }

    }



    //Boudnary Conditions
    public int[][] period(int x, int y)
    {
        if(x==0 && y==0){
            neighbours[0][0] = this.mesh[n-1][m-1];
            neighbours[0][1] = this.mesh[n-1][y];
            neighbours[0][2] = this.mesh[n-1][y+1];
            neighbours[1][0] = this.mesh[x][m-1];
            neighbours[1][1] = this.mesh[x][y];
            neighbours[1][2] = this.mesh[x][y+1];
            neighbours[2][0] = this.mesh[x+1][m-1];
            neighbours[2][1] = this.mesh[x+1][y];
            neighbours[2][2] = this.mesh[x+1][y+1];
        }
        else if(x==this.n-1 && y==0){
            neighbours[0][0] = this.mesh[x-1][m-1];
            neighbours[0][1] = this.mesh[x-1][y];
            neighbours[0][2] = this.mesh[x-1][y+1];
            neighbours[1][0] = this.mesh[x][m-1];
            neighbours[1][1] = this.mesh[x][y];
            neighbours[1][2] = this.mesh[x][y+1];
            neighbours[2][0]    = this.mesh[0][m-1];
            neighbours[2][1] = this.mesh[0][y];
            neighbours[2][2] = this.mesh[0][y+1];
        }
        else if(x==0 && y==this.m-1){
            neighbours[0][0] = this.mesh[n-1][y-1];
            neighbours[0][1] = this.mesh[n-1][y];
            neighbours[0][2] = this.mesh[n-1][0];
            neighbours[1][0] = this.mesh[x][y-1];
            neighbours[1][1] = this.mesh[x][y];
            neighbours[1][2] = this.mesh[x][0];
            neighbours[2][0] = this.mesh[x+1][y-1];
            neighbours[2][1] = this.mesh[x+1][y];
            neighbours[2][2] = this.mesh[x+1][0];
        }
        else if(x==this.n-1 && y==this.m-1){
            neighbours[0][0] = this.mesh[x-1][y-1];
            neighbours[0][1] = this.mesh[x-1][y];
            neighbours[0][2] = this.mesh[x-1][0];
            neighbours[1][0] = this.mesh[x][y-1];
            neighbours[1][1] = this.mesh[x][y];
            neighbours[1][2] = this.mesh[x][0];
            neighbours[2][0] = this.mesh[0][y-1];
            neighbours[2][1] = this.mesh[0][y];
            neighbours[2][2] = this.mesh[0][0];
        }
        else if(x==0 && y>0 && y<this.m-1){
            neighbours[0][0] = this.mesh[n-1][y-1];
            neighbours[0][1] = this.mesh[n-1][y];
            neighbours[0][2] = this.mesh[n-1][y+1];;
            neighbours[1][0] = this.mesh[x][y-1];
            neighbours[1][1] = this.mesh[x][y];
            neighbours[1][2] = this.mesh[x][y+1];
            neighbours[2][0] = this.mesh[x+1][y-1];
            neighbours[2][1] = this.mesh[x+1][y];
            neighbours[2][2] = this.mesh[x+1][y+1];
        }
        else if(x>0 && x<this.n-1 && y==0){
            neighbours[0][0] = this.mesh[x-1][m-1];
            neighbours[0][1] = this.mesh[x-1][y];
            neighbours[0][2] = this.mesh[x-1][y+1];
            neighbours[1][0] = this.mesh[x][m-1];
            neighbours[1][1] = this.mesh[x][y];
            neighbours[1][2] = this.mesh[x][y+1];
            neighbours[2][0] = this.mesh[x+1][m-1];
            neighbours[2][1] = this.mesh[x+1][y];
            neighbours[2][2] = this.mesh[x+1][y+1];
        }
        else if(x==this.n-1 && y>0 && y<this.m-1){
            neighbours[0][0] = this.mesh[x-1][y-1];
            neighbours[0][1] = this.mesh[x-1][y];
            neighbours[0][2] = this.mesh[x-1][y+1];
            neighbours[1][0] = this.mesh[x][y-1];
            neighbours[1][1] = this.mesh[x][y];
            neighbours[1][2] = this.mesh[x][y+1];
            neighbours[2][0] = this.mesh[0][y-1];
            neighbours[2][1] = this.mesh[0][y];
            neighbours[2][2] = this.mesh[0][y+1];
        }
        else if(y==this.m-1 && x>0 && x<this.n-1){
            neighbours[0][0] = this.mesh[x-1][y-1];
            neighbours[0][1] = this.mesh[x-1][y];
            neighbours[0][2] = this.mesh[x-1][0];
            neighbours[1][0] = this.mesh[x][y-1];
            neighbours[1][1] = this.mesh[x][y];
            neighbours[1][2] = this.mesh[x][0];
            neighbours[2][0] = this.mesh[x+1][y-1];
            neighbours[2][1] = this.mesh[x+1][y];
            neighbours[2][2] = this.mesh[x+1][0];
        }
        else{
            neighbours[0][0] = this.mesh[x-1][y-1];
            neighbours[0][1] = this.mesh[x-1][y];
            neighbours[0][2] = this.mesh[x-1][y+1];
            neighbours[1][0] = this.mesh[x][y-1];
            neighbours[1][1] = this.mesh[x][y];
            neighbours[1][2] = this.mesh[x][y+1];
            neighbours[2][0] = this.mesh[x+1][y-1];
            neighbours[2][1] = this.mesh[x+1][y];
            neighbours[2][2] = this.mesh[x+1][y+1];
        }

        return neighbours;
    }

    public int[][] absorption(int x, int y)
    {
        if(x==0 && y==0){
            neighbours[0][0] = 0;
            neighbours[0][1] = 0;
            neighbours[0][2] = 0;

            neighbours[1][0] = 0;
            neighbours[1][1] = this.mesh[x][y];
            neighbours[1][2] = this.mesh[x][y+1];

            neighbours[2][0] = 0;
            neighbours[2][1] = this.mesh[x+1][y];
            neighbours[2][2] = this.mesh[x+1][y+1];
        }
        else if(x==this.n-1 && y==0){
            neighbours[0][0] = 0;
            neighbours[0][1] = this.mesh[x-1][y];
            neighbours[0][2] = this.mesh[x-1][y+1];
            neighbours[1][0] = 0;
            neighbours[1][1] = this.mesh[x][y];
            neighbours[1][2] = this.mesh[x][y+1];
            neighbours[2][0] = 0;
            neighbours[2][1] = 0;
            neighbours[2][2] = 0;
        }
        else if(x==0 && y==this.m-1){
            neighbours[0][0] = 0;
            neighbours[0][1] = 0;
            neighbours[0][2] = 0;
            neighbours[1][0] = this.mesh[x][y-1];
            neighbours[1][1] = this.mesh[x][y];
            neighbours[1][2] = 0;
            neighbours[2][0] = this.mesh[x+1][y-1];
            neighbours[2][1] = this.mesh[x+1][y];
            neighbours[2][2] = 0;
        }

        else if(x==this.n-1 && y==this.m-1){
            neighbours[0][0] = this.mesh[x-1][y-1];
            neighbours[0][1] = this.mesh[x-1][y];
            neighbours[0][2] = 0;
            neighbours[1][0] = this.mesh[x][y-1];
            neighbours[1][1] = this.mesh[x][y];
            neighbours[1][2] = 0;
            neighbours[2][0] = 0;
            neighbours[2][1] = 0;
            neighbours[2][2] = 0;
        }
        else if(x==0 && y>0 && y<this.m-1){
            neighbours[0][0] = 0;
            neighbours[0][1] = 0;
            neighbours[0][2] = 0;
            neighbours[1][0] = this.mesh[x][y-1];
            neighbours[1][1] = this.mesh[x][y];
            neighbours[1][2] = this.mesh[x][y+1];
            neighbours[2][0] = this.mesh[x+1][y-1];
            neighbours[2][1] = this.mesh[x+1][y];
            neighbours[2][2] = this.mesh[x+1][y+1];
        }
        else if(x>0 && x<this.n-1 && y==0){
            neighbours[0][0] = 0;
            neighbours[0][1] = this.mesh[x-1][y];
            neighbours[0][2] = this.mesh[x-1][y+1];
            neighbours[1][0] = 0;
            neighbours[1][1] = this.mesh[x][y];
            neighbours[1][2] = this.mesh[x][y+1];
            neighbours[2][0] = 0;
            neighbours[2][1] = this.mesh[x+1][y];
            neighbours[2][2] = this.mesh[x+1][y+1];
        }
        else if(x==this.n-1 && y>0 && y<this.m-1){
            neighbours[0][0] = this.mesh[x-1][y-1];
            neighbours[0][1] = this.mesh[x-1][y];
            neighbours[0][2] = this.mesh[x-1][y+1];
            neighbours[1][0] = this.mesh[x][y-1];
            neighbours[1][1] = this.mesh[x][y];
            neighbours[1][2] = this.mesh[x][y+1];
            neighbours[2][0] = 0;
            neighbours[2][1] = 0;
            neighbours[2][2] = 0;
        }
        else if(y==this.m-1 && x>0 && x<this.n-1){
            neighbours[0][0] = this.mesh[x-1][y-1];
            neighbours[0][1] = this.mesh[x-1][y];
            neighbours[0][2] = 0;
            neighbours[1][0] = this.mesh[x][y-1];
            neighbours[1][1] = this.mesh[x][y];
            neighbours[1][2] = 0;
            neighbours[2][0] = this.mesh[x+1][y-1];
            neighbours[2][1] = this.mesh[x+1][y];
            neighbours[2][2] = 0;
        }
        else{
            neighbours[0][0] = this.mesh[x-1][y-1];
            neighbours[0][1] = this.mesh[x-1][y];
            neighbours[0][2] = this.mesh[x-1][y+1];
            neighbours[1][0] = this.mesh[x][y-1];
            neighbours[1][1] = this.mesh[x][y];
            neighbours[1][2] = this.mesh[x][y+1];
            neighbours[2][0] = this.mesh[x+1][y-1];
            neighbours[2][1] = this.mesh[x+1][y];
            neighbours[2][2] = this.mesh[x+1][y+1];
        }

        return neighbours;
    }

    public boolean[][] periodRecrystalization(int x, int y)
    {
        if(x==0 && y==0){
            neighboursRe[0][0] = this.PreviousRecrystalizaton.get(Integer.toString(n-1)+"|"+Integer.toString(m-1));
            neighboursRe[0][1] = this.PreviousRecrystalizaton.get(Integer.toString(n-1)+"|"+Integer.toString(y));
            neighboursRe[0][2] = this.PreviousRecrystalizaton.get(Integer.toString(n-1)+"|"+Integer.toString(y+1));
            neighboursRe[1][0] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(m-1));
            neighboursRe[1][1] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursRe[1][2] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursRe[2][0] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(m-1));
            neighboursRe[2][1] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursRe[2][2] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y+1));

        }
        else if(x==this.n-1 && y==0){
            neighboursRe[0][0] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(m-1));
            neighboursRe[0][1] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursRe[0][2] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y+1));
            neighboursRe[1][0] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(m-1));
            neighboursRe[1][1] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursRe[1][2] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursRe[2][0] = this.PreviousRecrystalizaton.get(Integer.toString(0)+"|"+Integer.toString(m-1));
            neighboursRe[2][1] = this.PreviousRecrystalizaton.get(Integer.toString(0)+"|"+Integer.toString(y));
            neighboursRe[2][2] = this.PreviousRecrystalizaton.get(Integer.toString(0)+"|"+Integer.toString(y+1));
        }
        else if(x==0 && y==this.m-1){
            neighboursRe[0][0] = this.PreviousRecrystalizaton.get(Integer.toString(n-1)+"|"+Integer.toString(y-1));
            neighboursRe[0][1] = this.PreviousRecrystalizaton.get(Integer.toString(n-1)+"|"+Integer.toString(y));
            neighboursRe[0][2] = this.PreviousRecrystalizaton.get(Integer.toString(n-1)+"|"+Integer.toString(0));
            neighboursRe[1][0] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursRe[1][1] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursRe[1][2] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(0));
            neighboursRe[2][0] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y-1));
            neighboursRe[2][1] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursRe[2][2] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(0));
        }
        else if(x==this.n-1 && y==this.m-1){
            neighboursRe[0][0] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y-1));
            neighboursRe[0][1] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursRe[0][2] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(0));
            neighboursRe[1][0] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursRe[1][1] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursRe[1][2] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(0));
            neighboursRe[2][0] = this.PreviousRecrystalizaton.get(Integer.toString(0)+"|"+Integer.toString(y-1));
            neighboursRe[2][1] = this.PreviousRecrystalizaton.get(Integer.toString(0)+"|"+Integer.toString(y));
            neighboursRe[2][2] = this.PreviousRecrystalizaton.get(Integer.toString(0)+"|"+Integer.toString(0));
        }
        else if(x==0 && y>0 && y<this.m-1){
            neighboursRe[0][0] = this.PreviousRecrystalizaton.get(Integer.toString(n-1)+"|"+Integer.toString(y-1));
            neighboursRe[0][1] = this.PreviousRecrystalizaton.get(Integer.toString(n-1)+"|"+Integer.toString(y));
            neighboursRe[0][2] = this.PreviousRecrystalizaton.get(Integer.toString(n-1)+"|"+Integer.toString(y+1));
            neighboursRe[1][0] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursRe[1][1] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursRe[1][2] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursRe[2][0] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y-1));
            neighboursRe[2][1] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursRe[2][2] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y+1));
        }
        else if(x>0 && x<this.n-1 && y==0){
            neighboursRe[0][0] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(m-1));
            neighboursRe[0][1] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursRe[0][2] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y+1));
            neighboursRe[1][0] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(m-1));
            neighboursRe[1][1] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursRe[1][2] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursRe[2][0] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(m-1));
            neighboursRe[2][1] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursRe[2][2] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y+1));
        }
        else if(x==this.n-1 && y>0 && y<this.m-1){
            neighboursRe[0][0] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y-1));
            neighboursRe[0][1] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursRe[0][2] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y+1));
            neighboursRe[1][0] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursRe[1][1] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursRe[1][2] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursRe[2][0] = this.PreviousRecrystalizaton.get(Integer.toString(0)+"|"+Integer.toString(y-1));
            neighboursRe[2][1] = this.PreviousRecrystalizaton.get(Integer.toString(0)+"|"+Integer.toString(y));
            neighboursRe[2][2] = this.PreviousRecrystalizaton.get(Integer.toString(0)+"|"+Integer.toString(y+1));
        }
        else if(y==this.m-1 && x>0 && x<this.n-1){
            neighboursRe[0][0] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y-1));
            neighboursRe[0][1] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursRe[0][2] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(0));
            neighboursRe[1][0] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursRe[1][1] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursRe[1][2] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(0));
            neighboursRe[2][0] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y-1));
            neighboursRe[2][1] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursRe[2][2] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(0));
        }
        else{
            neighboursRe[0][0] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y-1));
            neighboursRe[0][1] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursRe[0][2] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y+1));
            neighboursRe[1][0] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursRe[1][1] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursRe[1][2] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursRe[2][0] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y-1));
            neighboursRe[2][1] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursRe[2][2] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y+1));
        }

        return neighboursRe;
    }

    public boolean[][] absorptionRecrystalization(int x, int y)
    {
        if(x==0 && y==0){
            neighboursRe[0][0] = false;
            neighboursRe[0][1] = false;
            neighboursRe[0][2] = false;
            neighboursRe[1][0] = false;
            neighboursRe[1][1] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursRe[1][2] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursRe[2][0] = false;
            neighboursRe[2][1] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursRe[2][2] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y+1));
        }
        else if(x==this.n-1 && y==0){
            neighboursRe[0][0] = false;
            neighboursRe[0][1] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursRe[0][2] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y+1));
            neighboursRe[1][0] = false;
            neighboursRe[1][1] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursRe[1][2] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursRe[2][0] = false;
            neighboursRe[2][1] = false;
            neighboursRe[2][2] = false;
        }
        else if(x==0 && y==this.m-1){
            neighboursRe[0][0] = false;
            neighboursRe[0][1] = false;
            neighboursRe[0][2] = false;
            neighboursRe[1][0] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursRe[1][1] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursRe[1][2] = false;
            neighboursRe[2][0] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y-1));
            neighboursRe[2][1] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursRe[2][2] = false;
        }

        else if(x==this.n-1 && y==this.m-1){
            neighboursRe[0][0] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y-1));
            neighboursRe[0][1] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursRe[0][2] = false;
            neighboursRe[1][0] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursRe[1][1] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursRe[1][2] = false;
            neighboursRe[2][0] = false;
            neighboursRe[2][1] = false;
            neighboursRe[2][2] = false;
        }
        else if(x==0 && y>0 && y<this.m-1){
            neighboursRe[0][0] = false;
            neighboursRe[0][1] = false;
            neighboursRe[0][2] = false;
            neighboursRe[1][0] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursRe[1][1] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursRe[1][2] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursRe[2][0] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y-1));
            neighboursRe[2][1] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursRe[2][2] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y+1));
        }
        else if(x>0 && x<this.n-1 && y==0){
            neighboursRe[0][0] = false;
            neighboursRe[0][1] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursRe[0][2] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y+1));
            neighboursRe[1][0] = false;
            neighboursRe[1][1] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursRe[1][2] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursRe[2][0] = false;
            neighboursRe[2][1] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursRe[2][2] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y+1));

        }
        else if(x==this.n-1 && y>0 && y<this.m-1){
            neighboursRe[0][0] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y-1));
            neighboursRe[0][1] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursRe[0][2] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y+1));
            neighboursRe[1][0] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursRe[1][1] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursRe[1][2] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursRe[2][0] = false;
            neighboursRe[2][1] = false;
            neighboursRe[2][2] = false;
        }
        else if(y==this.m-1 && x>0 && x<this.n-1){
            neighboursRe[0][0] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y-1));
            neighboursRe[0][1] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursRe[0][2] = false;
            neighboursRe[1][0] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursRe[1][1] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursRe[1][2] = false;
            neighboursRe[2][0] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y-1));
            neighboursRe[2][1] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursRe[2][2] = false;
        }
        else{
            neighboursRe[0][0] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y-1));
            neighboursRe[0][1] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursRe[0][2] = this.PreviousRecrystalizaton.get(Integer.toString(x-1)+"|"+Integer.toString(y+1));
            neighboursRe[1][0] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursRe[1][1] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursRe[1][2] = this.PreviousRecrystalizaton.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursRe[2][0] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y-1));
            neighboursRe[2][1] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursRe[2][2] = this.PreviousRecrystalizaton.get(Integer.toString(x+1)+"|"+Integer.toString(y+1));

        }

        return neighboursRe;

    }

    public double[][] periodDyslocation(int x, int y)
    {
        if(x==0 && y==0){
            neighboursDys[0][0] = this.RoMap.get(Integer.toString(n-1)+"|"+Integer.toString(m-1));
            neighboursDys[0][1] = this.RoMap.get(Integer.toString(n-1)+"|"+Integer.toString(y));
            neighboursDys[0][2] = this.RoMap.get(Integer.toString(n-1)+"|"+Integer.toString(y+1));
            neighboursDys[1][0] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(m-1));
            neighboursDys[1][1] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursDys[1][2] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursDys[2][0] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(m-1));
            neighboursDys[2][1] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursDys[2][2] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y+1));

        }
        else if(x==this.n-1 && y==0){
            neighboursDys[0][0] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(m-1));
            neighboursDys[0][1] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursDys[0][2] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y+1));
            neighboursDys[1][0] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(m-1));
            neighboursDys[1][1] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursDys[1][2] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursDys[2][0] = this.RoMap.get(Integer.toString(0)+"|"+Integer.toString(m-1));
            neighboursDys[2][1] = this.RoMap.get(Integer.toString(0)+"|"+Integer.toString(y));
            neighboursDys[2][2] = this.RoMap.get(Integer.toString(0)+"|"+Integer.toString(y+1));
        }
        else if(x==0 && y==this.m-1){
            neighboursDys[0][0] = this.RoMap.get(Integer.toString(n-1)+"|"+Integer.toString(y-1));
            neighboursDys[0][1] = this.RoMap.get(Integer.toString(n-1)+"|"+Integer.toString(y));
            neighboursDys[0][2] = this.RoMap.get(Integer.toString(n-1)+"|"+Integer.toString(0));
            neighboursDys[1][0] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursDys[1][1] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursDys[1][2] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(0));
            neighboursDys[2][0] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y-1));
            neighboursDys[2][1] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursDys[2][2] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(0));
        }
        else if(x==this.n-1 && y==this.m-1){
            neighboursDys[0][0] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y-1));
            neighboursDys[0][1] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursDys[0][2] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(0));
            neighboursDys[1][0] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursDys[1][1] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursDys[1][2] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(0));
            neighboursDys[2][0] = this.RoMap.get(Integer.toString(0)+"|"+Integer.toString(y-1));
            neighboursDys[2][1] = this.RoMap.get(Integer.toString(0)+"|"+Integer.toString(y));
            neighboursDys[2][2] = this.RoMap.get(Integer.toString(0)+"|"+Integer.toString(0));
        }
        else if(x==0 && y>0 && y<this.m-1){
            neighboursDys[0][0] = this.RoMap.get(Integer.toString(n-1)+"|"+Integer.toString(y-1));
            neighboursDys[0][1] = this.RoMap.get(Integer.toString(n-1)+"|"+Integer.toString(y));
            neighboursDys[0][2] = this.RoMap.get(Integer.toString(n-1)+"|"+Integer.toString(y+1));
            neighboursDys[1][0] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursDys[1][1] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursDys[1][2] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursDys[2][0] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y-1));
            neighboursDys[2][1] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursDys[2][2] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y+1));
        }
        else if(x>0 && x<this.n-1 && y==0){
            neighboursDys[0][0] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(m-1));
            neighboursDys[0][1] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursDys[0][2] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y+1));
            neighboursDys[1][0] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(m-1));
            neighboursDys[1][1] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursDys[1][2] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursDys[2][0] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(m-1));
            neighboursDys[2][1] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursDys[2][2] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y+1));
        }
        else if(x==this.n-1 && y>0 && y<this.m-1){
            neighboursDys[0][0] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y-1));
            neighboursDys[0][1] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursDys[0][2] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y+1));
            neighboursDys[1][0] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursDys[1][1] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursDys[1][2] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursDys[2][0] = this.RoMap.get(Integer.toString(0)+"|"+Integer.toString(y-1));
            neighboursDys[2][1] = this.RoMap.get(Integer.toString(0)+"|"+Integer.toString(y));
            neighboursDys[2][2] = this.RoMap.get(Integer.toString(0)+"|"+Integer.toString(y+1));
        }
        else if(y==this.m-1 && x>0 && x<this.n-1){
            neighboursDys[0][0] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y-1));
            neighboursDys[0][1] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursDys[0][2] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(0));
            neighboursDys[1][0] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursDys[1][1] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursDys[1][2] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(0));
            neighboursDys[2][0] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y-1));
            neighboursDys[2][1] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursDys[2][2] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(0));
        }
        else{
            neighboursDys[0][0] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y-1));
            neighboursDys[0][1] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursDys[0][2] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y+1));
            neighboursDys[1][0] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursDys[1][1] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursDys[1][2] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursDys[2][0] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y-1));
            neighboursDys[2][1] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursDys[2][2] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y+1));
        }

        return neighboursDys;
    }

    public double[][] absorbtionDyslocation(int x, int y)
    {
        if(x==0 && y==0){
            neighboursDys[0][0] = 0.0;
            neighboursDys[0][1] = 0.0;
            neighboursDys[0][2] = 0.0;
            neighboursDys[1][0] = 0.0;
            neighboursDys[1][1] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursDys[1][2] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursDys[2][0] = 0.0;
            neighboursDys[2][1] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursDys[2][2] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y+1));
        }
        else if(x==this.n-1 && y==0){
            neighboursDys[0][0] = 0.0;
            neighboursDys[0][1] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursDys[0][2] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y+1));
            neighboursDys[1][0] = 0.0;
            neighboursDys[1][1] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursDys[1][2] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursDys[2][0] = 0.0;
            neighboursDys[2][1] = 0.0;
            neighboursDys[2][2] = 0.0;
        }
        else if(x==0 && y==this.m-1){
            neighboursDys[0][0] = 0.0;
            neighboursDys[0][1] = 0.0;
            neighboursDys[0][2] = 0.0;
            neighboursDys[1][0] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursDys[1][1] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursDys[1][2] = 0.0;
            neighboursDys[2][0] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y-1));
            neighboursDys[2][1] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursDys[2][2] = 0.0;
        }

        else if(x==this.n-1 && y==this.m-1){
            neighboursDys[0][0] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y-1));
            neighboursDys[0][1] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursDys[0][2] = 0.0;
            neighboursDys[1][0] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursDys[1][1] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursDys[1][2] = 0.0;
            neighboursDys[2][0] = 0.0;
            neighboursDys[2][1] = 0.0;
            neighboursDys[2][2] = 0.0;
        }
        else if(x==0 && y>0 && y<this.m-1){
            neighboursDys[0][0] = 0.0;
            neighboursDys[0][1] = 0.0;
            neighboursDys[0][2] = 0.0;
            neighboursDys[1][0] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursDys[1][1] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursDys[1][2] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursDys[2][0] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y-1));
            neighboursDys[2][1] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursDys[2][2] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y+1));
        }
        else if(x>0 && x<this.n-1 && y==0){
            neighboursDys[0][0] = 0.0;
            neighboursDys[0][1] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursDys[0][2] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y+1));
            neighboursDys[1][0] = 0.0;
            neighboursDys[1][1] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursDys[1][2] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursDys[2][0] = 0.0;
            neighboursDys[2][1] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursDys[2][2] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y+1));

        }
        else if(x==this.n-1 && y>0 && y<this.m-1){
            neighboursDys[0][0] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y-1));
            neighboursDys[0][1] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursDys[0][2] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y+1));
            neighboursDys[1][0] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursDys[1][1] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursDys[1][2] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursDys[2][0] = 0.0;
            neighboursDys[2][1] = 0.0;
            neighboursDys[2][2] = 0.0;
        }
        else if(y==this.m-1 && x>0 && x<this.n-1){
            neighboursDys[0][0] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y-1));
            neighboursDys[0][1] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursDys[0][2] = 0.0;
            neighboursDys[1][0] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursDys[1][1] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursDys[1][2] = 0.0;
            neighboursDys[2][0] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y-1));
            neighboursDys[2][1] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursDys[2][2] = 0.0;
        }
        else{
            neighboursDys[0][0] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y-1));
            neighboursDys[0][1] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y));
            neighboursDys[0][2] = this.RoMap.get(Integer.toString(x-1)+"|"+Integer.toString(y+1));
            neighboursDys[1][0] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y-1));
            neighboursDys[1][1] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y));
            neighboursDys[1][2] = this.RoMap.get(Integer.toString(x)+"|"+Integer.toString(y+1));
            neighboursDys[2][0] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y-1));
            neighboursDys[2][1] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y));
            neighboursDys[2][2] = this.RoMap.get(Integer.toString(x+1)+"|"+Integer.toString(y+1));

        }

        return neighboursDys;
    }



}
 