package cz.zcu.kiv.nlp.ir;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

  private static final String STOPWORDS_DEFAULT_PATH = "stopwords-cs.txt";

  public static void main(String[] args) throws IOException, ParseException {
    // 0. Specify the analyzer for tokenizing text.
    StopwordsLoader stopwordsLoader = new StopwordsLoader(STOPWORDS_DEFAULT_PATH);
    final var stopwords = stopwordsLoader.loadStopwords();
    Analyzer analyzer = new CzechAnalyzer(stopwords);

    // 1. create the index
    Directory index = IndexDirectoryType.FILE_BASED.getDirectoryInstance();

    if (!DirectoryReader.indexExists(index)) {
      IndexWriterConfig config = new IndexWriterConfig(analyzer);

      IndexWriter w = new IndexWriter(index, config);
      addDoc(w, "Lucene in Action", "193398817");
      addDoc(w, "Lucene for Dummies", "55320055Z");
      addDoc(w, "Managing Gigabytes", "55063554A");
      addDoc(w, "The Art of Computer Science", "9900333X");
      w.close();

      final var articles = loadArticles();
      createHokejIndex(analyzer, index, articles);
    }

    // 2. query
    String querystr = args.length > 0 ? args[0] : "lucene";

    // the "title" arg specifies the default field to use
    // when no field is explicitly specified in the query.
    Query q = new QueryParser("title", analyzer).parse(querystr);

    // 3. search
    int hitsPerPage = 10;
    IndexReader reader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(reader);
    TopDocs docs = searcher.search(q, hitsPerPage);
    ScoreDoc[] hits = docs.scoreDocs;

    // 4. display results
    System.out.println("Found " + hits.length + " hits.");
    for (int i = 0; i < hits.length; ++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      System.out.println((i + 1) + ". " + d.get("author") + "\t" + d.get("title"));
    }

    // reader can only be closed when there
    // is no need to access the documents any more.
    reader.close();
  }

  private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
    Document doc = new Document();
    doc.add(new TextField("title", title, Field.Store.YES));

    // use a string field for isbn because we don't want it tokenized
    doc.add(new StringField("isbn", isbn, Field.Store.YES));
    w.addDocument(doc);
  }

  private static List<Article> loadArticles() {
    return List.of(new Article("Třebíč ovládla základní část, Šumperk padá do druhé ligy! Zlín už je v šestce",
        "Adam Bagar", "25. února 18:52",
        """
            Šumperk po třech sezonách opouští Chance ligu, definitivně o tom rozhodla
            domácí porážka 0:3 s Kolínem. Na opačném pólu tabulky se raduje Třebíč, která
            po famózním obratu na Lapači poprvé v historii ovládla základní část soutěže. V
            50. kole uspěly také Jihlava, Litoměřice, Prostějov, Zlín a Poruba.

            Jihlava natáhla šňůru, Zubry skolil Šik <>

            Přerov z předchozích tří vzájemných duelů v sezoně urval sedm bodů a dlouho se
            zdálo, že přidá další. Po trefěMatouše Kratochvila <> dlouho vedl, jenže poté
            Daniel Kolář <> v přesilové hře vyrovnal a Otakar Šik <> v čase 59:29 dokonal
            parádní jihlavský obrat. Dukla natáhla bodovou šňůru na šest zápasů a na šestku
            ztrácí čtyři body. Zubrům, kteří prohráli šesté z posledních sedmi utkání, se
            přiblížila už na dva.

            HC DUKLA JIHLAVA – HC ZUBR PŘEROV 2:1 (0:0, 0:1, 2:0)


            Branky a nahrávky: 45. D. Kolář (Karabáček, Haman), 60. Šik (Kočí, Pořízek) –
            26. Kratochvil.
            Rozhodčí: Svoboda, Jechoutek – Kleprlík, Roupec.
            Vyloučení: 3:5.
            Využití: 1:0.
            Střely na branku: 29:28.
            Diváci: 634.
            HC Dukla Jihlava: Beran – D. Kolář, Haman, Bilčík, F. Kočí, Vala, A. Dvořák,
            Vovsik – T. Havránek, Skořepa, Karabáček – Pořízek, Handl, Šik – Cachnín, Juda,
            V. Brož – Křehlík. Trenér: Ujčík.
            HC ZUBR Přerov: Postava – Hrdinka, Chroboček, Krisl, Ševčík, F. Němec, R.
            Černý, Gréč – Březina, Pechanec, Indrák – Doležal, Macuh, Okál – Ministr,
            Kratochvil, Jakub Svoboda – Goiš, F. Dvořák, Jan Svoboda. Trenér: R. Svoboda.


             <>  <>

             <> play-rounded-fill play-rounded-outline play-sharp-fill play-sharp-outline
            <> pause-sharp-outline pause-sharp-fill pause-rounded-outline pause-rounded-fill
             <> 00:00  <>  <>Reklama <>
            Odkaz na video
            zkopírovat
            Embed videa
            zkopírovat SESTŘIH: Jihlava skolila Přerov gólem z 60. minuty <>
            <https://www.facebook.com/sharer/sharer.php?u=https%3A%2F%2Fwww.hokej.cz%2Ftv%2Fhokejka%2Fvideo%2F20222814>

            <https://twitter.com/home?status=https%3A%2F%2Fwww.hokej.cz%2Ftv%2Fhokejka%2Fvideo%2F20222814>
            Osmík parádní akcí rozhodl prodloužení
            <https://www.hokej.cz/tv/hokejka/video/20222283>Bitka Pohla s Lichtagem
            <https://www.hokej.cz/tv/hokejka/video/20221784>Kofroň v premiéře za Sokolov
            skóroval <https://www.hokej.cz/tv/hokejka/video/20221783>Boleslavský junior
            Půček (1+1) poprvé bodoval mezi dospělými
            <https://www.hokej.cz/tv/hokejka/video/20221765>


            Draci po třech letech opouští Chance ligu <>

            Šumperské naděje na záchranu definitivně vyhasly. Draci doma proti Kolínu
            nedali ani gól a po šesté porážce za sebou už s jistotou sestupují do třetí
            nejvyšší soutěže, s Chance ligou se loučí po třech letech. Kozlové přetavili ve
            vítězství střeleckou převahu 41:19 a dál živí šanci na předkolo play off, na
            desítku ztrácejí jen bod. V oslabení rozhodlŠtěpán Matějček <>, Jakub Soukup <>
            zapsal druhou nulu v sezoně.

            DRACI PARS ŠUMPERK – SC MARIMEX KOLÍN 0:3 (0:0, 0:1, 0:2)


            Branky a nahrávky: 26. Matějček (J. Sklenář, Zajíc), 45. K. Lang (D. Šedivý),
            60. Z. Král (Martin Novák, Naar).
            Rozhodčí: Pilný, Jaroš – Beneš, Peluha.
            Vyloučení: 3:6.
            Bez využití.
            V oslabení: 0:1.
            Střely na branku: 19:41.
            Diváci: 1 001.
            Draci Pars Šumperk: Hamalčík – Eliáš, Kunst, Dudycha, Hrachovský, Kadeřávek –
            Průžek, Bartoš, Žálčík – Matěj Svoboda, Šuhaj, Š. Jelínek – Mácha, Nedvídek,
            Vachutka. Trenér: Málek.
            SC Marimex Kolín: Soukup – Aubrecht, Gaspar, Piegl, Kukla, Zajíc, T. Jelínek,
            Naar – Martin Novák, Koukal, Z. Král – J. Sklenář, Matějček, Kadlec – D.
            Šedivý, Morong, K. Lang – Jiruš, Moravec, Pajer. Trenér: Šťastný.


             <>  <>

             <> play-rounded-fill play-rounded-outline play-sharp-fill play-sharp-outline
            <> pause-sharp-outline pause-sharp-fill pause-rounded-outline pause-rounded-fill
             <> 00:00  <>  <>Reklama <>
            Odkaz na video
            zkopírovat
            Embed videa
            zkopírovat SESTŘIH: Šumperk nedal Kolínu gól a padá do 2. ligy <>
            <https://www.facebook.com/sharer/sharer.php?u=https%3A%2F%2Fwww.hokej.cz%2Ftv%2Fhokejka%2Fvideo%2F20222818>

            <https://twitter.com/home?status=https%3A%2F%2Fwww.hokej.cz%2Ftv%2Fhokejka%2Fvideo%2F20222818>
            Osmík parádní akcí rozhodl prodloužení
            <https://www.hokej.cz/tv/hokejka/video/20222283>Bitka Pohla s Lichtagem
            <https://www.hokej.cz/tv/hokejka/video/20221784>Kofroň v premiéře za Sokolov
            skóroval <https://www.hokej.cz/tv/hokejka/video/20221783>Boleslavský junior
            Půček (1+1) poprvé bodoval mezi dospělými
            <https://www.hokej.cz/tv/hokejka/video/20221765>


            Play off se Sokolovu nadále vzdaluje <>

            Sokolov z předchozích tří partií s Litoměřicemi vytěžil plný počet devíti
            bodů, další tři se mu hodily v boji o desítku. Vedl 1:0, 2:1, 3:2, ale nakonec
            vyšel potřetí v řadě naprázdno a play off se mu vzdaluje, dvě kola před koncem
            na něj ztrácí pět bodů. Stadion ve třetí části otočil za 101 sekund a po pěti
            střetnutích slavil zisk tří bodů –David Senčák <> svým prvním gólem vyrovnal a
            Josef Jícha <> dokonal obrat.

            HC BANÍK SOKOLOV – HC STADION LITOMĚŘICE 3:4 (1:0, 2:2, 0:2)


            Branky a nahrávky: 17. Křemen (Csamangó, Kverka), 28. Csamangó (J. Novák,
            Klejna), 33. Zadražil (Chrpa, Csamangó) – 24. Berka (Procházka, Výtisk), 28. P.
            Svoboda (Válek, Hampl), 49. Senčák (J. Koláček), 51. Jícha (Válek, Černohorský).
            Rozhodčí: Kostourek, Micka – Kokrment, Dědek.
            Vyloučení: 3:2.
            Využití: 0:1.
            Střely na branku: 25:33.
            Diváci: 574.
            HC Baník Sokolov: Cichoň – J. Novák, Klejna, Weinhold, Rulík, Tomek, Baláž,
            Benda – Hauser, Kverka, Helt – Csamangó, Zadražil, Chrpa – Hajný, T. Rohan,
            Švec – Křemen, Kysela. Trenér: Štrba.
            HC Stadion Litoměřice: T. Král – Jebavý, Hampl, Š. Němec, Výtisk, Kroupa,
            Černohorský – Procházka, Kuťák, Guman – Válek, Jícha, P. Svoboda – Ton, J.
            Koláček, Berka – Slavíček, M. Vitouch, Senčák. Trenér: Přerost.


             <>  <>

             <> play-rounded-fill play-rounded-outline play-sharp-fill play-sharp-outline
            <> pause-sharp-outline pause-sharp-fill pause-rounded-outline pause-rounded-fill
             <> 00:00  <>  <>Reklama <>
            Odkaz na video
            zkopírovat
            Embed videa
            zkopírovat SESTŘIH: Litoměřice během 101 sekund otočily v Sokolově <>
            <https://www.facebook.com/sharer/sharer.php?u=https%3A%2F%2Fwww.hokej.cz%2Ftv%2Fhokejka%2Fvideo%2F20222815>

            <https://twitter.com/home?status=https%3A%2F%2Fwww.hokej.cz%2Ftv%2Fhokejka%2Fvideo%2F20222815>
            Osmík parádní akcí rozhodl prodloužení
            <https://www.hokej.cz/tv/hokejka/video/20222283>Bitka Pohla s Lichtagem
            <https://www.hokej.cz/tv/hokejka/video/20221784>Kofroň v premiéře za Sokolov
            skóroval <https://www.hokej.cz/tv/hokejka/video/20221783>Boleslavský junior
            Půček (1+1) poprvé bodoval mezi dospělými
            <https://www.hokej.cz/tv/hokejka/video/20221765>


            Prostějov se blýskl obratem z 0:3 na 6:4 <>

            Když Jaroslav Brož <> zvyšoval na startu druhé třetiny už na 3:0 pro Slavii, v
            tu chvíli by si na Prostějov nejspíš nevsadili ani ti nejvěrnější příznivci.
            Ale Jestřábi předvedli fantastickou otočku, kterou dokonali čtyřmi trefami ve
            třetí třetině. Vítězství 6:4 řídil tříbodovýJakub Illéš <> (2+1), dvakrát se
            trefil takéTomáš Jiránek <>. Jestřábi si upevnili čtyřku, Slavia je devátá, ale
            jen tři body od jedenáctého Kolína.

            LHK JESTŘÁBI PROSTĚJOV – HC SLAVIA PRAHA 6:4 (0:2, 2:1, 4:1)


            Branky a nahrávky: 26. Jiránek (Vlach, Kubeš), 34. Veselý (Poledna, Illéš),
            44. Illéš (Bartko, Veselý), 51. Jiránek, 52. Kubeš (Slanina, Maruna), 60. Illéš
            (Venkrbec, Poledna) – 15. Kulda (J. Brož, Klikorka), 20. Mrňa (Žovinec), 22. J.
            Brož (Všetečka, Žovinec), 42. Klikorka (Kulda).
            Rozhodčí: Koziol, Rapák – Kučera, Bezděk.
            Vyloučení: 7:7.
            Využití: 2:2.
            Střely na branku: 40:29.
            Diváci: 1 210.
            LHK Jestřábi Prostějov: Pavelka – Poledna, Kubeš, Malák, Bartko, Valenta,
            Hamšík – Jiránek, T. Jáchym, Slanina – Jandus, Vlach, Hašek – Pěnčík, Venkrbec,
            Maruna – Veselý, Illéš. Trenér: Totter.
            HC Slavia Praha: Málek – Klikorka, Hejda, Žovinec, Křepelka, Barák, Szathmáry,
            Fiala – Strnad, Žejdl, Kulda – Všetečka, L. Stehlík, J. Brož – Mrňa, Simon,
            Curran – Havrda, Labuzík, Krliš. Trenér: Tvrzník.


             <>  <>

             <> play-rounded-fill play-rounded-outline play-sharp-fill play-sharp-outline
            <> pause-sharp-outline pause-sharp-fill pause-rounded-outline pause-rounded-fill
             <> 00:00  <>  <>Reklama <>
            Odkaz na video
            zkopírovat
            Embed videa
            zkopírovat SESTŘIH: Prostějov předvedl proti Slavii famózní obrat <>
            <https://www.facebook.com/sharer/sharer.php?u=https%3A%2F%2Fwww.hokej.cz%2Ftv%2Fhokejka%2Fvideo%2F20222817>

            <https://twitter.com/home?status=https%3A%2F%2Fwww.hokej.cz%2Ftv%2Fhokejka%2Fvideo%2F20222817>
            Osmík parádní akcí rozhodl prodloužení
            <https://www.hokej.cz/tv/hokejka/video/20222283>Bitka Pohla s Lichtagem
            <https://www.hokej.cz/tv/hokejka/video/20221784>Kofroň v premiéře za Sokolov
            skóroval <https://www.hokej.cz/tv/hokejka/video/20221783>Boleslavský junior
            Půček (1+1) poprvé bodoval mezi dospělými
            <https://www.hokej.cz/tv/hokejka/video/20221765>


            Třebíč otočila šlágr a přepisuje historii <>


            Vsetín měl ve šlágru kola našlápnuto k výhře, ještě v 54. minutě vedl 3:1, ale
            Třebíč ukázala famózní závěrečný obrat, jenž v prodloužení dokonal svým druhým
            gólem v utkáníLadislav Bittner <>. Horácká Slavia natáhla bodovou šňůru na
            dvanáct utkání a dvě kola před koncem už je jasné, že ji nikdo nedožene –
            poprvé v historii ovládla základní část Chance ligy. Valaši se vracejí na třetí
            flek, o skóre za Porubu.

            VHK ROBE VSETÍN – SK HORÁCKÁ SLAVIA TŘEBÍČ 3:4 v prodloužení (1:1, 1:0, 1:2 –
            0:1)


            Branky a nahrávky: 11. Sihvonen (Smetana, Bērziņš), 28. Holec (Jenáček, Rob),
            46. Rob (Holec, Bērziņš) – 20. Bittner (Dočekal, Furch), 54. Malý (Matyáš
            Svoboda, Klímek), 57. Vodný (D. Michálek, Forman), 63. Bittner (Dočekal).
            Rozhodčí: Skopal, Cabák – Roischel, Blažek.
            Vyloučení: 0:2.
            Využití: 1:0.
            Střely na branku: 20:33.
            Diváci: 2 681.
            VHK ROBE Vsetín: Žukov – Kajínek, Jenáček, O. Němec, Česánek, Smetana,
            Ondračka – Holec, Bērziņš, Rob – Sihvonen, Přikryl, Š. Bláha – Číp, Lichanec,
            Jenyš – Melenovský, Vávra, Pětník. Trenér: Weintritt.
            SK Horácká Slavia Třebíč: Jekel – Bořuta, Furch, Forman, Fajmon, Poizl,
            Vodička – Vodný, D. Michálek, Ferda – Dočekal, Bittner, Psota – Klímek, Malý,
            I. Sedláček – Matyáš Svoboda, Pšenička, Matěj Novák. Trenér: Pokorný.


             <>  <>

             <> play-rounded-fill play-rounded-outline play-sharp-fill play-sharp-outline
            <> pause-sharp-outline pause-sharp-fill pause-rounded-outline pause-rounded-fill
             <> 00:00  <>  <>Reklama <>
            Odkaz na video
            zkopírovat
            Embed videa
            zkopírovat SESTŘIH: Třebíč po obratu na Lapači ovládla základní část <>
            <https://www.facebook.com/sharer/sharer.php?u=https%3A%2F%2Fwww.hokej.cz%2Ftv%2Fhokejka%2Fvideo%2F20222816>

            <https://twitter.com/home?status=https%3A%2F%2Fwww.hokej.cz%2Ftv%2Fhokejka%2Fvideo%2F20222816>
            Osmík parádní akcí rozhodl prodloužení
            <https://www.hokej.cz/tv/hokejka/video/20222283>Bitka Pohla s Lichtagem
            <https://www.hokej.cz/tv/hokejka/video/20221784>Kofroň v premiéře za Sokolov
            skóroval <https://www.hokej.cz/tv/hokejka/video/20221783>Boleslavský junior
            Půček (1+1) poprvé bodoval mezi dospělými
            <https://www.hokej.cz/tv/hokejka/video/20221765>


            Berani jsou v šestce, Dynamo záchráněno <>

            Zlín využil dalšího zaváhání Přerova a čtvrtou výhrou v řadě už se posunul do
            elitní šestky, která rovnou postoupí do čtvrtfinále. Proti Dynamu se ale
            nadřel, o jeho vítězství rozhodla jediná trefa, o kterou se ve třetí části
            postaral v přesilovceDenis Kindl <>. Daniel Huf <> kryl všech 21 střel a zapsal
            čtvrtou nulu,Milan Klouček <> chytil 40 z 41 pokusů a přes fotbalovou porážku
            může se spoluhráči slavit záchranu.

            PSG BERANI ZLÍN – HC DYNAMO PARDUBICE B 1:0 (0:0, 0:0, 1:0)


            Branky a nahrávky: 48. Kindl (Z. Sedlák, Suhrada).
            Rozhodčí: Zubzanda, Veselý – Vašíček, Komínek.
            Vyloučení: 6:4.
            Využití: 1:0.
            Střely na branku: 41:21.
            Diváci: 2 799.
            PSG Berani Zlín: Huf – Husa, Gazda, Suhrada, Zbořil, Riedl, Talafa –
            Kratochvíl, Kindl, Z. Sedlák – M. Lang, Gago, Mrázek – Kubiš, Hejcman, Pospíšil
            – P. Sedláček, Köhler, Sadovikov. Trenér: Říha.
            HC Dynamo Pardubice B: Klouček – Bukač, Hanousek, Nedbal, Bučko, Chabada,
            Hrádek, Drtina – O. Rohlík, Rákos, Lichtag – Kaplan, Hruška, T. Svoboda –
            Matýs, T. Urban, Rouha – M. Machač, T. Zeman, Pochobradský. Trenér: Baďouček.


             <>  <>

             <> play-rounded-fill play-rounded-outline play-sharp-fill play-sharp-outline
            <> pause-sharp-outline pause-sharp-fill pause-rounded-outline pause-rounded-fill
             <> 00:00  <>  <>Reklama <>
            Odkaz na video
            zkopírovat
            Embed videa
            zkopírovat SESTŘIH: Zlín je v šestce, na Dynamo stačil gól Kindla <>
            <https://www.facebook.com/sharer/sharer.php?u=https%3A%2F%2Fwww.hokej.cz%2Ftv%2Fhokejka%2Fvideo%2F20222820>

            <https://twitter.com/home?status=https%3A%2F%2Fwww.hokej.cz%2Ftv%2Fhokejka%2Fvideo%2F20222820>
            Osmík parádní akcí rozhodl prodloužení
            <https://www.hokej.cz/tv/hokejka/video/20222283>Bitka Pohla s Lichtagem
            <https://www.hokej.cz/tv/hokejka/video/20221784>Kofroň v premiéře za Sokolov
            skóroval <https://www.hokej.cz/tv/hokejka/video/20221783>Boleslavský junior
            Půček (1+1) poprvé bodoval mezi dospělými
            <https://www.hokej.cz/tv/hokejka/video/20221765>


            Frýdek-Místek porubské oslavy nepřekazil <>

            Slezskému derby předcházel v rámci oslav 90 let porubského hokeje souboj
            tamních a vítkovických legend. Slavnostní duel před 3128 diváky začal s drobným
            zpožděním, pak se RT TORAX do rivala zakousl, přestřílel ho jasně 54:23 a
            slavil vítězství, které jej posunulo zpátky na druhé místo tabulky o skóre před
            Vsetín. Frýdek-Místek uzavírá postupovou desítku, ale jedenáctý Kolín na něj
            ztrácí jen bod.

            HC RT TORAX PORUBA 2011 – HC FRÝDEK-MÍSTEK 4:2 (1:0, 3:2, 0:0)


            Branky a nahrávky: 14. Bernovský (A. Sedlák, Střondala), 26. Dufek (Pšurný,
            Šlahař), 31. Urbanec (Šlahař, Dufek), 39. Hrníčko (Střondala, Bernovský) – 30.
            Doktor (Dostálek, Homola), 40. Ramik (Matěj, Homola).
            Rozhodčí: Hucl, Mejzlík – Otáhal, Veselý.
            Vyloučení: 1:3, navíc Vachovec – Havránek oba 5 minut.
            Bez využití.
            Střely na branku: 54:23.
            Diváci: 3 128.
            HC RT TORAX Poruba 2011: O. Bláha – Urbanec, Voráček, Klimíček, Lemcke, A.
            Sedlák, Doudera, Mlčák – Gřeš, Vachovec, Šoustal – Šlahař, Pšurný, Dufek –
            Bernovský, Střondala, Hrníčko – Karafiát, Toman, Häring. Trenér: Režnar.
            HC Frýdek-Místek: Mokry – Žůrek, Teper, Š. Havránek, J. Michálek, Sova,
            Homola, J. Adámek – Svačina, A. Zeman, Macháček – Doktor, Vrdlovec, Dostálek –
            Hotěk, Haas, Klimša – Geidl, Matěj, Ramik. Trenér: Janeček.


             <>  <>

             <> play-rounded-fill play-rounded-outline play-sharp-fill play-sharp-outline
            <> pause-sharp-outline pause-sharp-fill pause-rounded-outline pause-rounded-fill
             <> 00:00  <>  <>Reklama <>
            Odkaz na video
            zkopírovat
            Embed videa
            zkopírovat SESTŘIH: Poruba oslavila 90 let vítězstvím v derby <>
            <https://www.facebook.com/sharer/sharer.php?u=https%3A%2F%2Fwww.hokej.cz%2Ftv%2Fhokejka%2Fvideo%2F20222819>

            <https://twitter.com/home?status=https%3A%2F%2Fwww.hokej.cz%2Ftv%2Fhokejka%2Fvideo%2F20222819>
            Osmík parádní akcí rozhodl prodloužení
            <https://www.hokej.cz/tv/hokejka/video/20222283>Bitka Pohla s Lichtagem
            <https://www.hokej.cz/tv/hokejka/video/20221784>Kofroň v premiéře za Sokolov
            skóroval <https://www.hokej.cz/tv/hokejka/video/20221783>Boleslavský junior
            Půček (1+1) poprvé bodoval mezi dospělými
            <https://www.hokej.cz/tv/hokejka/video/20221765>


            Tabulka Chance ligy

             TýmZVVPPPPSkóreBody
            1. SK Horácká Slavia Třebíč 50 28 6 3 13 141:108 99
            2. HC RT TORAX Poruba 2011 50 23 9 4 14 163:129 91
            3. VHK ROBE Vsetín 50 29 1 2 18 147:118 91
            4. LHK Jestřábi Prostějov 50 24 4 8 14 146:135 88
            5. HC Stadion Litoměřice 50 21 6 6 17 156:143 81
            6. PSG Berani Zlín 50 21 5 5 19 136:123 78
            7. HC ZUBR Přerov 50 18 5 12 15 127:119 76
            8. HC Dukla Jihlava 50 20 4 6 20 125:140 74
            9. HC Slavia Praha 50 19 5 4 22 143:138 71
            10. HC Frýdek-Místek 50 18 5 5 22 129:132 69
            11. SC Marimex Kolín 50 17 6 5 22 139:146 68
            12. HC Baník Sokolov 50 17 4 5 24 128:164 64
            13. HC Dynamo Pardubice B 50 11 8 5 26 129:152 54
            14. Draci Pars Šumperk 50 9 7 5 29 130:192 46
            Další program Chance ligy

            Pondělí 27. února – 51. kolo:
            17:30 SK Horácká Slavia Třebíč – HC Stadion Litoměřice
            17:30 HC RT TORAX Poruba 2011 – Draci Pars Šumperk
            18:00 HC Frýdek-Místek – PSG Berani Zlín
            18:00 HC Dynamo Pardubice B – HC Dukla Jihlava
            18:00 HC Slavia Praha – VHK ROBE Vsetín
            18:00 SC Marimex Kolín – HC Baník Sokolov
            18:00 HC ZUBR Přerov – LHK Jestřábi Prostějov

            Středa 1. března – 52. kolo:
            17:30 HC Dukla Jihlava – HC Slavia Praha
            17:30 VHK ROBE Vsetín – SC Marimex Kolín
            17:30 HC Stadion Litoměřice – HC Frýdek-Místek
            17:30 PSG Berani Zlín – HC RT TORAX Poruba 2011
            18:00 LHK Jestřábi Prostějov – SK Horácká Slavia Třebíč
            18:00 HC Baník Sokolov – HC ZUBR Přerov
            18:00 Draci Pars Šumperk – HC Dynamo Pardubice B



            Více k tématu

             * Třída a bída: Hejda = spolehlivost, Novák = disciplína <>
             * Hokej v číslech: Prvoligový fenomén je zpátky! <>
             * Šmicer, Koller, Baroš a další. V Kolíně se bude hrát Dobrý hokej <>
             * Zlín útočí na šestku! Třebíč sviští za triumfem v základní části, Šumperk
            do 2. ligy <>
             * Poruba slaví 90 let od založení. Láká na městské derby legend, kdo se
            zúčastní? <>

             <http://twitter.com/share>

              """));
  }

  private static void createHokejIndex(Analyzer analyzer, Directory index, List<Article> articles) {
    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    try (IndexWriter w = new IndexWriter(index, config)) {
      w.addDocuments(articles.stream().map(Article::toDocument).collect(Collectors.toList()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
