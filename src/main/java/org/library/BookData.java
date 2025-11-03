package org.library;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public enum BookData {
    GREAT_GATSBY("Great Gatsby", "F. Scott FitzGerald"),
    RED_RISING("Red Rising", "Pierce Brown"),
    THE_WAY_OF_KINGS("The Ways of Kings", "Brandon Sanderson"),
    A_GAME_OF_THRONES("A Game of Thrones", "George R.R. Martin"),
    THE_HANDMAIDS_TALE("The Handmaid's Tale", "Margaret Atwood"),
    NINETEEN_EIGHTY_FOUR("Nineteen Eighty-Four", "George Orwell"),
    THE_APOTHECARY_DIARIES_VOL_1("The Apothecary Diaries: Volume 1", "Natsu Hyuga"),
    THE_SCIENCE_OF_BEAUTY("The Science of Beauty", "Michelle Wong"),
    NO_LONGER_HUMAN("No Longer Human", "Osamu Dazai"),
    ERAGON("Eragon", "Christopher Paolini"),
    MOBY_DICK("Moby-Dick", "Herman Melville"),
    SUPERGIRL_WOMAN_OF_TOMORROW_1("Supergirl: Woman of Tomorrow #1", "Tom King"),
    ABSOLUTE_BATMAN_1("Absolute Batman #1", "Scott Snyder"),
    BERSERK_DELUXE_VOL_1("Berserk Deluxe Volume 1", "Kentaro Miura"),
    TO_KILL_A_MOCKINGBIRD("To Kill a Mockingbird", "Harper Lee"),
    THE_ART_AND_MAKING_OF_ARCANE("The Art and Making of Arcane", "Elisabeth Vincentelli"),
    THE_HUNGER_GAMES("The Hunger Games", "Suzanne Collins"),
    BLOOD_MERIDIAN("Blood Meridian", "Cormac McCarthy"),
    CRIME_AND_PUNISHMENT("Crime and Punishment", "Fyodor Dostoevsky"),
    A_SPACE_ODYSSEY("2001: A Space Odyssey", "Arthur C. Clarke"),
    THE_GREAT_GATSBY("The Great Gatsby", "F. Scott FitzGerald"),
    PRIDE_AND_PREJUDICE("Pride and Prejudice", "Jane Austen"),
    THE_HOBBIT("The Hobbit", "J. R. R. Tolkien"),
    HARRY_POTTER("Harry Potter", "J. K. Rowling"),
    THE_CATCHER_IN_THE_RYE("The Catcher in the Rye", "J. D. Salinger"),
    ANIMAL_FARM("Animal Farm", "George Orwell"),
    LORD_OF_THE_FLIES("Lord of the Flies", "William Golding"),
    JANE_EYRE("Jane Eyre", "Charlotte Brontë"),
    WUTHERING_HEIGHTS("Wuthering Heights", "Emily Brontë"),
    THE_ODYSSEY("The Odyssey", "Homer"),
    HAMLET("Hamlet", "William Shakespeare"),
    WAR_AND_PEACE("War and Peace", "Leo Tolstoy"),
    THE_DIVINE_COMEDY("The Divine Comedy", "Dante Alighieri"),
    DON_QUIXOTE("Don Quixote", "Miguel de Cervantes"),
    THE_ILIAD("The Iliad", "Homer"),
    ULYSSES("Ulysses", "James Joyce");

    private final String title;
    private final String author;

    BookData(String title, String author){
        this.title = title;
        this.author = author;
    }

    public String getTitle(){ return title; }
    public String getAuthor(){ return author; }

    public static List<BookData> getArrSortedByAuthor(){
        ArrayList<BookData> result = new ArrayList<>(Arrays.asList(BookData.values()));
        result.sort(Comparator.comparing(BookData::getAuthor));
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s by %s", getTitle(), getAuthor());
    }
}
