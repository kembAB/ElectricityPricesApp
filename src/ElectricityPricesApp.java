import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ElectricityPricesApp {

    private static boolean ismenurunning = true;
    private static final Scanner scanner = new Scanner(System.in);

    private static PricePoint[] unitprices;

    public static void main(String[] args) {

        while (ismenurunning) {

            printMainMenu();


            handleMainMenuChoice(getUserInput());
        }
    }

    private static void printMainMenu() {

        System.out.println();
        System.out.println("Electricity prices");
        System.out.println();
        System.out.println("1. Insert price");
        System.out.println("2. Min, Max och Average");
        System.out.println("3. Sort Increasing order");
        System.out.println("4. Best car charging interval(4h)");
        System.out.println("e. Exit program");
        System.out.println();
        System.out.print("what would you like to choose?: ");
    }


    private static String getUserInput() {
        return scanner.nextLine();
    }


    private static void handleMainMenuChoice(String input) {
        switch (input) {
            case "E", "e" -> quitApp();
            case "1" -> Insertprices();
            case "2" -> printStatistics();
            case "3" -> PrintSortedIncreasingOrder();
            case "4" -> printOptimalTimeToCharge();
            default -> handleIllegalInputValue();


        }
    }


    private static void Insertprices() {

        System.out.println("\n please insert price in all intervals of the day \n");

        unitprices = new PricePoint[24];

        for (int i = 0; i < 24; i++) {

            String timeInterval = PricePoint.formatTimeInterval(i, 1);
            System.out.printf("%s : ", timeInterval);
            String userInput = getUserInput();
            boolean inputIsValid = validateDataInput(userInput);

            if (inputIsValid) unitprices[i] = new PricePoint(timeInterval, Integer.parseInt(userInput));
            else {
                System.out.println();
                handleIllegalInputValue();
                try {
                    Thread.sleep(50);
                } catch (Exception ignored) {}
                i--;
            }
        }

    }

    private static boolean validateDataInput(String input) {

        Pattern regexp = Pattern.compile("^\\d{1,4}$");
        return regexp.matcher(input).find();
    }

    private static void printStatistics() {

        if (unitprices == null)
            displayExceptionErrorMsg();
        else {
            double avgPrice = PricePoint.calcAverage(unitprices);
            System.out.printf("\naverage price: \n\t%.2f SEK/kWh\n", (avgPrice+0.5) / 100);

            PricePoint[] minMaxPricePoints = PricePoint.findMinMax(unitprices);
            PricePoint min = minMaxPricePoints[0];
            PricePoint max = minMaxPricePoints[1];
            System.out.printf("Lowest price : \n\t%s\n\t%.2f SEK/kWh\n", min.timeInterval(), ( min.price() / 100.0 ) );
            System.out.printf("Higest price : \n\t%s\n\t%.2f SEK/kWh\n", max.timeInterval(), ( max.price() / 100.0 ) );

            System.out.println();

            System.out.print("\n\n");
        }
    }

    private static void displayExceptionErrorMsg() {
        System.err.println("please Insert prices selecting menu 1,Insertprices \n");
    }

    private static void PrintSortedIncreasingOrder() {

        if (unitprices == null)
            displayExceptionErrorMsg();
        else {
            PricePoint[] sorted = Arrays.copyOf(unitprices, unitprices.length);
            Arrays.sort(sorted);
            System.out.println("\nPrices in an increasing order :");

            for (PricePoint pricePoint : sorted) {
                System.out.printf("%s  -->  %.2f SEK/kWh\n", pricePoint.timeInterval(), ( pricePoint.price() / 100.0 ) );
            }
        }
    }

    private static void printOptimalTimeToCharge() {

        if (unitprices == null)
            displayExceptionErrorMsg();
        else {
            int formerTotal = Arrays.stream(Arrays.copyOf(unitprices,4)).map(PricePoint::price).reduce(0,(total, price) -> total + price);

            int iOptimalStart = 0;
            int optimalTotal = formerTotal;

            for (int first = 1, last = 4; last < unitprices.length; first++, last++) {

                int contender = formerTotal - unitprices[first - 1].price() + unitprices[last].price();

                if (contender < optimalTotal) {
                    iOptimalStart = first;
                    optimalTotal = contender;
                }
                formerTotal = contender;
            }
            System.out.println("\n The best charging time is :");
            System.out.printf("\n%s\n", PricePoint.formatTimeInterval(iOptimalStart, 4));
            System.out.printf("Average price is : %.2f SEK\n", ( (optimalTotal / 4.0)+0.5 ) / 100.0);
        }
    }

    private static void quitApp() {
        ismenurunning = false;
    }

    private static void handleIllegalInputValue() {
        System.err.println("The input is invalid . please try agian \n");
    }


}

