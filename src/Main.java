interface Accessory {
    void applyEffect(GameCharacter character);
}

class StrengthRing implements Accessory {
    @Override
    public void applyEffect(GameCharacter character) {
        if (character instanceof Warrior warrior) {
            warrior.increaseStrength(5);
            warrior.increasePhysicDef(10);
            warrior.setSlashDamageBonus(1 + 0.05);
        }
    }
}

class Glove implements Accessory {
    @Override
    public void applyEffect(GameCharacter character) {
        if (character instanceof Wizard wizard) {
            wizard.increaseIntelligence(5);
            wizard.increaseMagicDef(10);
            wizard.setFireballBonus(1 + 0.05);
        }
    }
}

record Sword(double baseAtk) {
    public double getBaseAtk() {
        return baseAtk;
    }
}

record HeavyArmor(double baseDef) {
    public double getBaseDef() {
        return baseDef;
    }
}

class GameCharacter {
    public final double level;
    public double baseHp;

    public GameCharacter(double level, double baseHp) {
        this.level = level;
        this.baseHp = baseHp;
    }

    public double getLevel() {
        return level;
    }

    public double getBaseHp() {
        return baseHp;
    }
}

class Warrior extends GameCharacter {
    private double baseStr;
    private double baseDef;
    private Sword equippedSword;
    private HeavyArmor equippedHeavyArmor;
    private double slashDamageBonus = 0;
    private double Level;

    public Warrior(double level, double baseHp, double baseStr, double baseDef) {
        super(level, baseHp);
        this.baseStr = baseStr;
        this.baseDef = baseDef;
        updateStats();
    }

    public void equipAccessory(Accessory accessory) {
        accessory.applyEffect(this);
    }

    public void increaseStrength(double value) {
        this.baseStr += value;
    }

    public void increasePhysicDef(double value) {
        this.baseDef += value;
    }

    public void setSlashDamageBonus(double bonus) {
        this.slashDamageBonus = bonus;
    }

    private double calculateAtk() {
        double Atk = 35 + baseStr * 5;
        if (equippedSword != null) {
            Atk += equippedSword.getBaseAtk();
        }
        return Atk;
    }

    private void updateStats() {
        if (equippedHeavyArmor != null) {
            this.baseDef += equippedHeavyArmor.getBaseDef();
        }
        this.Level = level;
    }

    public void dmgTaken(double damage) {
        double actualDmg = Math.max(0, damage);
        this.baseHp -= actualDmg;

        if (baseHp <= 0) {
            this.baseHp = 0;
            System.out.println("Wizard death....");
        }
    }

    public void printStats() {
        System.out.println(
                "Warrior Lv: " + Level +
                        " | ATK: " + calculateAtk() +
                        " | HP: " + getMaxHp() +
                        " | STR: " + strength() +
                        " | DEF: " + getMaxDef() +
                        " | Slash Bonus: " + slashDamageBonus);
    }

    private double strength() {
        return baseStr + (1 * Level);
    }

    private double getMaxHp() {
        return baseHp ;
    }

    public double getMaxDef() {
        return baseDef;
    }

    public void equipSword(Sword sword) {
        this.equippedSword = sword;
        updateStats();
    }
    public void equipHeavyArmor(HeavyArmor heavyArmor) {
        this.equippedHeavyArmor = heavyArmor;
        updateStats();
    }
    public void PVP(Wizard wizard) {
        printStats();
        wizard.printWizardStats();
        System.out.println();

        double damageDealt = calculateAtk() - wizard.getMaxDef();
        double damageTaken = wizard.calculateAtk() - getMaxDef();

        if (damageDealt <= 0) {
            System.out.println("Warrior dealt 0 damage to the Wizard.");
        } else {
            wizard.dmgTaken(damageDealt);
            System.out.println("Warrior dealt " + damageDealt + " damage to the Wizard.");
        }

        if (damageTaken <= 0) {
            System.out.println("Wizard dealt 0 damage to the Warrior.");
        } else {
            dmgTaken(damageTaken);
            System.out.println("Warrior took " + damageTaken + " damage from the Wizard.");
        }

        if (wizard.getBaseHp() <= 0) {
            System.out.println();
            System.out.println("Wizard death....");
        }

        System.out.println();
    }

}

class Wizard extends GameCharacter {
    private final double wizardMp;
    private double wizardIntelligence;
    private double wizardDef;
    private double fireballBonus;

    public Wizard(double level, double wizardHp, double wizardMp, double wizardIntelligence, double wizardDef) {
        super(level, wizardHp);
        this.wizardMp = wizardMp;
        this.wizardIntelligence = wizardIntelligence;
        this.wizardDef = wizardDef;
    }
    public double getMaxDef() {
        return wizardDef;
    }
    public void dmgTaken(double damage) {
        double actualDmg = Math.max(0, damage);
        this.baseHp -= actualDmg;
        if (baseHp < 0) {
            this.baseHp = 0;
        }
    }

    public double calculateAtk() {
        return getLevel() * (3 + fireballBonus);
    }

    public void equipAccessory(Accessory accessory) {
        accessory.applyEffect(this);
    }

    public void increaseIntelligence(double value) {
        this.wizardIntelligence += value;
    }

    public void increaseMagicDef(double value) {
        this.wizardDef += value;
    }

    public void setFireballBonus(double bonus) {
        this.fireballBonus = bonus;
    }

    public void printWizardStats() {
        System.out.println(
                "Wizard Lv: " + level +
                        " | ATK: " + calculateAtk() +
                        " | HP: " + getBaseHp() +
                        " | MP: " + wizardMp +
                        " | INT: " + wizardIntelligence +
                        " | DEF: " + getMaxDef() +
                        " | Fireball Bonus: " + fireballBonus);
    }
}

class GameMain {
    public static void main(String[] args) {
        Sword sword = new Sword(180);
        HeavyArmor heavyArmor = new HeavyArmor(90);

        Warrior warrior = new Warrior(20, 800, 0, 20);
        Wizard wizard = new Wizard(50, 400, 100, 0, 40);

        warrior.PVP(wizard);

        System.out.println("Warrior equips Strength Ring");
        warrior.equipAccessory(new StrengthRing());
        warrior.equipAccessory(new Glove());

        System.out.println("Wizard equips Glove");
        wizard.equipAccessory(new Glove());
        System.out.println();

        warrior.PVP(wizard);

        System.out.println();
        System.out.println("Warrior gets Sword and Armor");
        System.out.println();
        warrior.equipSword(sword);
        warrior.equipHeavyArmor(heavyArmor);

        while (warrior.getBaseHp() > 0 && wizard.getBaseHp() > 0) {
            warrior.PVP(wizard);

            if (warrior.getBaseHp() <= 0) {
                System.out.println();
                System.out.println("Warrior Hp: 0");
                System.out.println("Warrior has been defeated!");
                break;
            }
            if (wizard.getBaseHp() <= 0) {
                System.out.println();
                System.out.println("Wizard Hp: 0.0 | Warrior Hp:" + warrior.getBaseHp());
                System.out.println("Warrior is victorious!");
                break;
            }
        }
    }
}
