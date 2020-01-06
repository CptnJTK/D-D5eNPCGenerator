package NPCGenerator;

import java.util.ArrayList;
import java.util.Random;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NPC {
    // Constants
    private final char[] VOWELS = { 'a', 'e', 'i', 'o', 'u'};
    private final String[][] GENDERS = { 
            {"female", "she", "her", "her", "hers", "herself"}, 
            {"feminine", "she", "them", "their", "theirs", "themself"}, 
            {"androgynous", "they", "them", "their", "theirs", "themself"}, 
            {"masculine", "he", "them", "their", "theirs", "themself"},
            {"male", "he", "him", "his", "his", "himself"}
        };
    private final String[] RACES = {
            "human",    // 64%  0  <= x <= 63   0
            "dwarf",    // 10%  64 <= x <= 73   1  
            "elf",      // 10%  74 <= x <= 83   2
            "halfling", // 6%   84 <= x <= 89   3
            "half-elf", // 5%   90 <= x <= 94   4 
            "gnome",    // 4%   95 <= x <= 98   5
            "half-orc"  // 1%      x == 99      6
    };
    private final String[] APPEARANCES = {
            "ornate earrings",
            "ornate necklace",
            "ornate circlet",
            "ornate bracelets",
            "piecrings",
            "outlandish clothes",
            "formal, clean clothes",
            "ragged, dirty clothes",
            "pronounced scar",
            "missing teeth",
            "missing fingers",
            "unusual eye colour",
            "dichromatic eyes",
            "tattoos",
            "birthmark",
            "unusual skin color",
            "bald head",
            "braided hair",
            "unusual hair color",
            "nervous eye twitch",
            "unusual looking nose",
            "rigid posture",
            "exceptional beauty",
            "exceptional ugliness" };
    private final String[] ABILITIES = {
            "strength",
            "dexterity",
            "constitution",
            "intelligence",
            "wisdom",
            "charisma" };
    private final String[] ABILITIES_HIGH = {
            "powerful, brawny, and strong as an ox",
            "lithe, agile, and graceful",
            "hardy, hale, and healthy",
            "studious, learned, and inquisitive",
            "perceptive, spiritual, and insightful",
            "tactful, persuasive, and forceful"};
    private final String[] ABILITIES_LOW = {
            "feeble and scrawny",
            "clumsy and fumbling",
            "sickly and pale",
            "dim-witted and slow",
            "oblivious and absentminded",
            "dull and boring" };
    private final String[] TALENTS = {
            "playing the bagpipes",
            "playing the drum",
            "playing the dulcimer",
            "playing the flute",
            "playing the horn",
            "playing the lute",
            "playing the lyre",
            "playing the pan flute",
            "playing the shawm",
            "playing the viol",
            "speaking dwarvish",
            "speaking elvish",
            "speaking giant",
            "speaking gnomish",
            "speaking goblin",
            "speaking halfling",
            "speaking orc",
            "bets of chance",
            "memorization",
            "handling animals",
            "handling children",
            "solving puzzles",
            "playing dice",
            "playing dragonchess",
            "playing cards",
            "playing three-dragon ante",
            "performing impersonations",
            "drawing",
            "painting",
            "singing",
            "drinking",
            "carpenting",
            "cooking",
            "dart throwing",
            "rock skipping",
            "juggling",
            "acting",
            "dancing" };
    private final String[] MANNERISMS = {
            "singing quietly to themself",
            "whistling to themself",
            "humming to themself",
            "speaking in rhyme",
            "speaking in a high pitch",
            "speaking in a low pitch",
            "slurring words",
            "stuttering",
            "enunciating overly clearly",
            "speaking loudly",
            "whispering",
            "using flowery speach",
            "using long words",
            "frequently using the wrong word",
            "using colorful exclamations",
            "making conatant jokes and puns",
            "making predictions of doom",
            "fidgeting",
            "squinting",
            "staring into the distance",
            "chewing something",
            "pacing",
            "tapping their fingers",
            "biting their fingernails",
            "twirling hair",
            "cracking knuckles" };
    private final String[] INTERACTIONS = {
            "argumentative",
            "arrogant",
            "blustering",
            "rude",
            "curious",
            "friendly",
            "honest",
            "hot tempered",
            "irritable",
            "ponderous",
            "quiet",
            "suspicious" };
    private final String[] IDEALS = {
            "beauty",       // GOOD
            "charity",
            "the greater good",
            "life",
            "respect",
            "self-sacrifice",
            "domination",   // EVIL
            "greed", 
            "might",
            "pain",
            "retribution",
            "bloodshed",
            "community",    // LAWFUL
            "fairness",
            "honor",
            "logic",
            "responsibility",
            "tradition",
            "change",       // CHAOTIC
            "creativity",
            "freedom",
            "independence",
            "autonomy",
            "whimsy",
            "balance",      // NEUTRAL
            "knowledge",
            "passivity",
            "moderation",
            "neutrality",
            "people",
            "aspiration",   // OTHER
            "discovery",
            "glory",
            "nationality",
            "redemption",
            "enlightenment" };
    private final String[] BONDS = {
            "dedication to fulfilling a personal life goal",
            "protectiveness of close family members",
            "protectiveness of colleagues or compatriots",
            "loyalty to a benefactor, patron, or employer",
            "captivation of a romantic interest",
            "attraction to a special place",
            "protectiveness of a sentimental keepsake",
            "protectiveness of a valuable possession",
            "pursuit of revenge",
            "ROLL TWICE" };
    private final String[] FLAWS_AND_SECRETS = {
            "has a susceptibility to romance",
            "enjoys decadent pleasures",
            "acts and thinks arrogantly",
            "envies another creature's possession",
            "possesses overpowering greed",
            "is prone to rage",
            "has a powerful enemy",
            "has a specific phobia",
            "has a shameful/scandalous history",
            "has comitted a secret crime or misdeed",
            "is in possession of forbidden lore",
            "possesses foolhardy bravery" };

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    // Object Properties
    private String name;
    private String info;
    private boolean saved;
    
    // Default Constructor
    public NPC(){
        try {
            String[] temp = generateOutput();
            this.name = temp[0];
            this.info = temp[1];
            this.saved = false;
        }catch(IOException e) {
        }
    }
    
    // Load constructor
        // Used by MainView.load to populate data we know is saved
    public NPC(String name, String info) {
        this.name = name;
        this.info = info;
        this.saved = true;
    }
    
    // Used by MainView.loadButton listener to load selected NPC into the spotlight
    public NPC load(NPC newNPC) {
        this.name = newNPC.name;
        this.info = newNPC.info;
        this.saved = true;
        this.pcs.firePropertyChange("NPC loaded", null, null);
        return newNPC;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
        this.pcs.firePropertyChange("Name Changed", null, null);
    }
    
    // Returns NPC info for multi-line label display
    public String getInfo() {
        return "<html>" + this.info.replaceAll(System.lineSeparator(), "<br/>") + "</html>";
    }
    
    // Returns NPC info for saving
    public String getRawInfo() {
        return this.info;
    }
    public void save() {
        this.saved = true;
        this.pcs.firePropertyChange("NPC Saved", null, null);
    }
    public void delete() {
        this.saved = false;
        this.pcs.firePropertyChange("NPC Deleted", null, null);
    }
    public boolean isSaved() {
        return this.saved;
    }
    
    private String roll(String[] array) {
        Random rand = new Random();
        return array[rand.nextInt(array.length)];
    }
    private String[] roll(String[][] array) {
        Random rand = new Random();
        return array[rand.nextInt(array.length)];
    }
    private boolean contains(final char[] array, char key) {
        for (int i = 0; i < array.length; i++) {
            if (key == array[i]) {
                return true;
            }
        }
        return false;
    }
    private String generateName(String gender, String race) throws IOException {
        Random rand = new Random();
        final String[] GENDER_BINARY = {"female", "male"};
        final String[] HALF_ELF_BINARY = {"human", "elf"};
        ArrayList<String> possibleNames = new ArrayList<String>();
        Charset charset = Charset.forName("US-ASCII");
        String name = "";
        // TODO redo to allow for custom genders and pronouns
        gender = (gender.equals("masculine")?"male":(gender.equals("feminine")?"female":gender.equals("androgynous")?GENDER_BINARY[rand.nextInt(2)]:gender));
        race = (race.equals("half-elf")?HALF_ELF_BINARY[rand.nextInt(2)]: race);
        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get(race+"_"+gender+".txt"), charset);
            String currentName;
            while ((currentName = reader.readLine())!=null) {
                possibleNames.add(currentName);
            }
            name += possibleNames.get(rand.nextInt(possibleNames.size()));
            reader.close();
            race = (race.equals("half-orc")? "human": race);
            possibleNames.clear();
            reader = Files.newBufferedReader(Paths.get(race+"_surname.txt"), charset);
            while ((currentName = reader.readLine())!=null) {
                possibleNames.add(currentName);
            }
            name+= " "+possibleNames.get(rand.nextInt(possibleNames.size()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(reader != null) {
                    reader.close();
                }
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        return name;
    }
    public String[] generateOutput() throws IOException {
        Random rand = new Random();
        String[] gender = roll(GENDERS); // 0 - Gender, 1 - Subject, 2 - Object, 3 - Possessive, 4 - Possessive Pronoun, 5 - Reflexive
        String race;
        int raceNum = rand.nextInt(100);
        if (raceNum <= 63) {
            race = RACES[0];
            raceNum = 0;
        } else if (raceNum <= 73) {
            race = RACES[1];
            raceNum = 1;
        } else if (raceNum <= 83) {
            race = RACES[2];
            raceNum = 2;
        } else if (raceNum <= 89) {
            race = RACES[3];
            raceNum = 3;
        } else if (raceNum <= 94) {
            race = RACES[4];
            raceNum = 4;
        } else if (raceNum <= 98) {
            race = RACES[5];
            raceNum = 5;
        } else {
            race = RACES[6];
            raceNum = 6;
        }
        
        String appearance = roll(APPEARANCES);
        
        int highAbilityNum = rand.nextInt(ABILITIES.length);
        String highAbility = ABILITIES[highAbilityNum];
        String highAbilityDesc = ABILITIES_HIGH[highAbilityNum];
        
        int lowAbilityNum;
        do {
            lowAbilityNum = rand.nextInt(ABILITIES.length);
        } while(lowAbilityNum == highAbilityNum);
        String lowAbility = ABILITIES[lowAbilityNum];
        String lowAbilityDesc = ABILITIES_LOW[lowAbilityNum];
        
        String talent = roll(TALENTS);
        
        String mannerism = roll(MANNERISMS);
        
        String interactions = roll(INTERACTIONS);
        
        String ideal = roll(IDEALS);
        
        String bond;
        int bondNum = rand.nextInt(BONDS.length);
        if (bondNum == BONDS.length-1) {
            int num1, num2;
            do {
                num1 = rand.nextInt(BONDS.length-1);
                num2 = rand.nextInt(BONDS.length-1);
            } while(num1 == num2);
            bond = BONDS[num1] + " and " + BONDS[num2];
        } else {
            bond = BONDS[bondNum];
        }
        
        String secret = roll(FLAWS_AND_SECRETS);
        String name = generateName(gender[0], race);
        
        String indefiniteArticle = (contains(VOWELS, gender[0].charAt(0)))? "an": "a";
        String out1 = "<b>"+name+"</b> is "+indefiniteArticle+" " + gender[0] + " " + race + ", visually distinguished by " + gender[3]+ " " + appearance + ". ";
        String out2 = name+" boasts exceptional "+ highAbility + " making "+ gender[2]+" "+ highAbilityDesc +", while simultaneously being "+ lowAbilityDesc+ " from "+gender[3]+" lacklustre "+lowAbility+". ";
        String out3 = name+" displays talent in " + talent +" and has a habit of "+mannerism+". ";
        String out4 = name+" typically acts "+interactions+" towards others but "+gender[1]+" prioritizes "+ideal+" over all else. ";
        String out5 = name+" is motivated by "+gender[3]+" "+ bond+". "+name+" secretly "+secret+".";
        String[] outArray = {name, out1 +  out2 +  out3 +  out4 + out5};
        return outArray;
    }

    public void regenerate() {
        try {
            String[] temp = generateOutput();
            this.name = temp[0];
            this.info = temp[1];
            this.saved = false;
            this.pcs.firePropertyChange("Regenerated", null, null);
        }catch(IOException e) {
        }
    }
    public NPC copy() {
        return new NPC(this.name, this.info);
    }
    public String toString() {
        return this.name;
    }
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }
}