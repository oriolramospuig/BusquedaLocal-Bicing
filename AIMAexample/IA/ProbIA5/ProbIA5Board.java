package IA.ProbIA5;

import IA.Bicing.Estacion;
import IA.Bicing.Estaciones;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import static java.lang.Math.*;
import java.util.HashMap;
import java.util.Map;

public class ProbIA5Board {

    public class Ruta {
        private Estacion estacionIni;
        private Estacion estacionFi1;
        private Estacion estacionFi2;
        private int nbicisRecogidas;
        private int nbicisDejadas1;
        private int nbicisDejadas2;

        private int costeRuta;



        public Ruta(Estacion e1, Estacion e2, Estacion e3, int n1, int n2, int n3, int c) {
            this.estacionIni = e1;
            this.estacionFi1 = e2;
            this.estacionFi2 = e3;
            this.nbicisRecogidas = n1;
            this.nbicisDejadas1 = n2;
            this.nbicisDejadas2 = n3;
            costeRuta = c;

        }

        public Estacion getEstacionInicial() {
            return estacionIni;
        }

        public Estacion getEstacionFinal1() {
            return estacionFi1;
        }

        public Estacion getEstacionFinal2() {
            return estacionFi2;
        }

        public int getNBicis() {
            return nbicisRecogidas;
        }

        public int getBicisRecogidas() {return nbicisRecogidas;}



        public int getBicisDejadas1() {
            return nbicisDejadas1;
        }

        public int getBicisDejadas2() {
            return nbicisDejadas2;
        }

        public int getCosteRuta() {
            return costeRuta;
        }

        public void setCosteRuta(int costeRuta) {
            this.costeRuta = costeRuta;
        }

        public void setEstacionIni(Estacion e1) {
            estacionIni = e1;
        }

        public void setEstacionFi1(Estacion e2) {
            estacionFi1 = e2;
        }

        public void setEstacionFi2(Estacion e3) {
            estacionFi2 = e3;
        }

        public void setNbicisDejadas2(int b) {
            nbicisDejadas2 = b;
        }


    }


    /*******************************/
    /***** ATRIBUTOS DE ESTADO *****/
    /*******************************/

    private static int nbicis;
    private static int nestaciones;

    private int nfurgos;
    private ArrayList<Ruta> Rutas;
    private Map<Estacion, Integer> estaciones;
    private float coste;




    /**************************/
    /******* CREADORAS ********/
    /**************************/



    public ProbIA5Board(Estaciones e, int nb, int nf) {
        estaciones = new HashMap<Estacion, Integer>();
        nbicis = nb;
        nestaciones = e.size();
        nfurgos = nf;
        for (int i = 0; i < e.size(); ++i) {
            estaciones.put(e.get(i), e.get(i).getNumBicicletasNext());
        }
        //estadoInicial1();
        estadoInicial2(e);


        coste = 0;

    }

    public ProbIA5Board(Map<Estacion, Integer> e, int nb, int nf, ArrayList<Ruta> r, float c) {
        estaciones = new HashMap<>();

        for (Map.Entry<Estacion, Integer> entry : e.entrySet()) {
            Estacion key = entry.getKey();
            Integer value = entry.getValue();
            estaciones.put(key, value);
        }


        nbicis = nb;
        nestaciones = e.size();
        nfurgos = nf;

        Rutas = new ArrayList<>(r.size());
        for (int i = 0; i < r.size(); ++i) {
            Rutas.add(copiarRuta(r.get(i)));
        }


        coste = c;
    }

    //Copia del pare amb totes les copies de rutes, excepte una que no s'afageix
    public ProbIA5Board(Map<Estacion, Integer> e, int nb, int nf, ArrayList<Ruta> r, float c,  Ruta noAñadir) {
        estaciones = new HashMap<>();

        for (Map.Entry<Estacion, Integer> entry : e.entrySet()) {
            Estacion key = entry.getKey();
            Integer value = entry.getValue();
            estaciones.put(key, value);
        }

        nbicis = nb;
        nestaciones = e.size();
        nfurgos = nf;

        Rutas = new ArrayList<>(r.size());
        for (Ruta ruta : r) {
            if (!ruta.equals(noAñadir)) Rutas.add(copiarRuta(ruta));
        }

        coste = c;
    }

    private Ruta copiarRuta(Ruta r) {
        return new Ruta(r.getEstacionInicial(), r.getEstacionFinal1(), r.getEstacionFinal2(),
                r.getBicisRecogidas(), r.getBicisDejadas1(), r.getBicisDejadas2(), r.getCosteRuta());
    }


    /*********************/
    /****** GETTERS ******/
    /*********************/


    //Devuelve en que posicion del vector estaciones se encuentra la estación con coordenadas x e y

    public int getNBicis() { return nbicis; }

    public int getNEstaciones() { return nestaciones; }

    public int getNFurgos() { return nfurgos; }

    public Map<Estacion, Integer> getEstaciones() { return estaciones; }

    public ArrayList<Ruta> getRutas(){ return Rutas; }
    public float getCoste(){ return coste; }

    public int getNRutas() { return Rutas.size(); };

    public Integer getBicisNext(Estacion e) {
        return estaciones.get(e);
    }


    /********************************/
    /****** ESTADOS INICIALES *******/
    /********************************/

    public boolean estadoInicial1() {
        Rutas = new ArrayList<Ruta>();
        return true;
    }

    public boolean estadoInicial2(Estaciones e) {

        Rutas = new ArrayList<>();

        for (int i = 0; i < nfurgos && i < nestaciones; i += 2) {
            Estacion e1 = e.get(i);
            Estacion e2 = e.get(i+1);

            if (bicisSobrantes(e1) != 0) {
                añadirFurgoneta(e1, e2, e2, bicisSobrantes(e1), bicisSobrantes(e1), 0);
                int bR = bicisSobrantes(e1);
                int bD = bicisSobrantes(e1);
                estaciones.put(e1, estaciones.get(e1) - bicisSobrantes(e1));
                estaciones.put(e2, estaciones.get(e2) + bicisSobrantes(e1));
                System.out.println("Furgo añadida Inicial"
                        +  ". C: " + coste
                        + ". D1, N1: " + e1.getDemanda() + " " + bR
                        + ". bR, bD1, bD2: " + bR + " " + bR + " " + 0
                        + ". D2, N2: " + e2.getDemanda() + " " + e2.getNumBicicletasNext());
            }
        }

        return false;
    }


    public void ordenarEstacionesPorDiferencia(List<Estacion> e) {
        Collections.sort(e, new Comparator<Estacion>() {
            @Override
            public int compare(Estacion estacion1, Estacion estacion2) {
                int diferencia1 = bicisNecesarias(estacion1);
                int diferencia2 = bicisNecesarias(estacion2);
                return Integer.compare(diferencia2, diferencia1);
            }
        });
    }

    /*************************/
    /****** OPERADORES *******/
    /*************************/

    //Nova ruta. De momento solo puede tener una estación final
    //Bicis recogidas <= nbicis en e1 && Bicis recogidas <= 30
    public void añadirFurgoneta(Estacion e1, Estacion e2, Estacion e3, Integer bR, Integer bD1, Integer bD2) {
        //si copiamos una ruta necesitamos su coste anterior, si es una ruta nueva coste empieza en cero
        Ruta rutaNueva = new Ruta(e1, e2, e3, bR, bD1, bD2, 0);

        modificarCoste(rutaNueva);

        estaciones.put(e1, getBicisNext(e1) - bR);

        estaciones.put(e2, getBicisNext(e2) + bD1);

        estaciones.put(e3, getBicisNext(e3) + bD2);




        Rutas.add(rutaNueva);
        //System.out.println("Nuevo coste: " + coste);
    }

    //c es el coste de la ruta antes de modificar alguno de sus aspectos, hay que recalcular el coste de esa ruta una vez
    //modificada la ruta
    public void modificarFurgoneta(Estacion e1, Estacion e2, Estacion e3, Integer bR, Integer bD1, Integer bD2, int c) {
        //si copiamos una ruta necesitamos su coste anterior, si es una ruta nueva coste empieza en cero
        Ruta rutaNueva = new Ruta(e1, e2, e3, bR, bD1, bD2, c);

        modificarCoste(rutaNueva);

        estaciones.put(e1, getBicisNext(e1) - bR);

        estaciones.put(e2, getBicisNext(e2) + bD1);

        estaciones.put(e3, getBicisNext(e3) + bD2);




        Rutas.add(rutaNueva);
        //System.out.println("Nuevo coste: " + coste);
    }

    /*
    public void cambiarEstacionInicial(Ruta ruta, Estacion newInicial) {
        //Ruta rutaNueva = new Ruta(e1, e2, e3, bicisRecogidas, bicisDejadas, bicisRecogidas - bicisDejadas);
        Ruta rutaNueva = new Ruta(newInicial, ruta.getEstacionFinal1(), ruta.getEstacionFinal2(), ruta.getBicisRecogidas(), ruta.getBicisDejadas1(), ruta.getBicisDejadas2());
        //System.out.println("Coste antes de la nueva ruta: " + coste);
        modificarCoste(rutaNueva);

        Rutas.add(rutaNueva);
        //System.out.println("Nuevo coste: " + coste);
    }*/

    /**************************/
    /****** MODIFICADORAS *****/
    /**************************/


    //Modificar coste amb l'operador canviar estacion inicial no te sentit
    public void modificarCoste(Ruta ruta) {
        coste -= ruta.getCosteRuta();
        ruta.setCosteRuta(0);

        int c = 0;

        //int bicicletasTransportadas = ruta.getBicisRecogidas() + ruta.getBicisDejadas1() + ruta.getBicisDejadas2();
        //c += ((bicicletasTransportadas + 9) / 10)*kilometros;

        //Nos beneficia dejar una bici en una estacion, mientras no se supere la demanda de bicis necesaria
        c -= min(ruta.getBicisDejadas1(), bicisNecesarias(ruta.getEstacionFinal1()));

        // si bicis dejadas 2 es 0 consideramos que no vamos a esa estacion
        if (ruta.getBicisDejadas2() != 0 && ruta.getEstacionFinal1() != ruta.getEstacionFinal2()) c -= min(ruta.getBicisDejadas2(), bicisNecesarias(ruta.getEstacionFinal2()));

        //Incrementa el coste por cada bici que recojamos por debajo de la demanda
        c += max(0, ruta.getBicisRecogidas() - bicisSobrantes(ruta.getEstacionInicial()));

        coste += c;
        ruta.setCosteRuta(c);
    }

    /*************************/
    /****** AUXILIARES *******/
    /*************************/

    public boolean rutaIniciaEnEstacion(Estacion estacion) {
        for (Ruta ruta : Rutas) {
            if (ruta.getEstacionInicial().equals(estacion)) {
                return true; // La ruta ya inicia en esta estación
            }
        }
        return false; // La estación no se usa como punto de inicio de ninguna ruta
    }

    //Bicis que faltan en una estacion
    public int bicisNecesarias(Estacion e) {
        return max(0, e.getDemanda() - getBicisNext(e));
    }

    //Bicis que sobran en una estacion
    public int bicisSobrantes(Estacion e) {
        return max(0, getBicisNext(e) - e.getDemanda());
    }

    //Distancia entre la estacion inicial y la estacion final 1

    //APUNTAR A L'INFORME LA NOSTRA REPRESENTACIO DE ESTACIO FINAL 2

    public int distanciaRecorrida(final Ruta ruta) {
        int dis = distanciaEstaciones(ruta.getEstacionInicial(), ruta.getEstacionFinal1());
        if (ruta.getBicisRecogidas() - ruta.getBicisDejadas1() > 0) dis += distanciaEstaciones(ruta.getEstacionFinal1(), ruta.getEstacionFinal2());
        return dis;
    }

    //Devuelve la distancia entre las dos estaciones
    public int distanciaEstaciones(Estacion e1, Estacion e2) {
        return abs(e1.getCoordX() - e2.getCoordX()) + abs(e1.getCoordY() - e2.getCoordY());

    }

    public int getBicisMal() {
        int valor = 0;
        for (Ruta r1: Rutas) {
            int aux = r1.getEstacionFinal1().getDemanda();
            int aux2 = r1.nbicisDejadas1;
            valor += aux2 - aux;
        }
        return min(0, valor);
    }
}
