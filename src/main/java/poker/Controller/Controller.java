package poker.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.exp;

@org.springframework.stereotype.Controller
public class Controller {

    Neuron firstNeuron;
    Neuron secondNeuron;
    Neuron thirdNeuron;

    boolean globalError;

    @RequestMapping("/")
    public String startGame(final Model model) {
        model.addAttribute("alpha", 0.1);
        model.addAttribute("iterations", 1000);
        return "index";
    }

    @RequestMapping("/learn")
    @ResponseBody
    public int networkLearning(@RequestParam("alpha") String alphaStr, @RequestParam("iterations") String iterationsStr) {
        final Random random = new Random();
        firstNeuron = new Neuron();
        firstNeuron.weights = new ArrayList<>();
        firstNeuron.weights.add(round(random.nextDouble()));
        firstNeuron.weights.add(round(random.nextDouble()));
        firstNeuron.weights.add(round(random.nextDouble()));
        secondNeuron = new Neuron();
        secondNeuron.weights = new ArrayList<>();
        secondNeuron.weights.add(round(random.nextDouble()));
        secondNeuron.weights.add(round(random.nextDouble()));
        secondNeuron.weights.add(round(random.nextDouble()));
        thirdNeuron = new Neuron();
        thirdNeuron.weights = new ArrayList<>();
        thirdNeuron.weights.add(round(random.nextDouble()));
        thirdNeuron.weights.add(round(random.nextDouble()));
        final double alpha = Double.parseDouble(alphaStr);
        final int iterations = Integer.valueOf(iterationsStr);
        ArrayList<double[]> trainingSet = getTrainingSet();
        int epoch = 0;
        do {
            globalError = false;
            epoch++;
            for(double[] inputArray : trainingSet) {
                train(alpha, inputArray);
            }
        }while(globalError && iterations > epoch);

        return epoch;
    }

    @RequestMapping("/play")
    @ResponseBody
    public String play(@RequestParam("aggro") String aggroStr, @RequestParam("bet") String betStr,
                       @RequestParam("cardPower") String cardPowerStr) {
        final double aggro = Double.parseDouble(aggroStr);
        final double bet = Double.parseDouble(betStr);
        final double cardPower = Double.parseDouble(cardPowerStr);
        double[] doubleInputs = new double[3];
        doubleInputs[0] = aggro;
        doubleInputs[1] = bet;
        doubleInputs[2] = cardPower;
        getResultOfActiveFunction(doubleInputs);
        if (thirdNeuron.resultOfActiveFunction >= 0.5) {
            return "Raise!";
        } else {
            return "Better to fold!";
        }
    }

    static double round(double number) {
        BigDecimal bd = new BigDecimal(number);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    private static double signoid(double sum) {
        return round(1 / (1 + exp(round(-sum))));
    }

    private void getResultOfActiveFunction(final double[] inputs) {
        double firstOutputSum = firstNeuron.weights.get(0) * inputs[0] + firstNeuron.weights.get(1) * inputs[1] + firstNeuron.weights.get(2) * inputs[2];
        firstNeuron.resultOfActiveFunction = signoid(firstOutputSum);

        double secondOutputSum = secondNeuron.weights.get(0) * inputs[0] + secondNeuron.weights.get(1) * inputs[1] + secondNeuron.weights.get(2) * inputs[2];
        secondNeuron.resultOfActiveFunction = signoid(secondOutputSum);

        double thirdOutputSum = thirdNeuron.weights.get(0) * firstNeuron.resultOfActiveFunction + thirdNeuron.weights.get(1) * secondNeuron.resultOfActiveFunction;
        thirdNeuron.resultOfActiveFunction = signoid(thirdOutputSum);
    }

    private void updateThirdNeuron(double weightsDelta, final double alpha) {
        thirdNeuron.weights.set(0, thirdNeuron.weights.get(0) - firstNeuron.resultOfActiveFunction * weightsDelta * alpha);
        thirdNeuron.weights.set(1, thirdNeuron.weights.get(1) - secondNeuron.resultOfActiveFunction * weightsDelta * alpha);
    }

    private void updateFirstNeuron(double weightsDelta, final double alpha, final double[] inputs) {
        double actual = firstNeuron.resultOfActiveFunction;
        double error = thirdNeuron.weights.get(0) * weightsDelta;
        double gradient = actual * (1 - actual);
        double newWeightsDelta = error * gradient;
        firstNeuron.weights.set(0, firstNeuron.weights.get(0) - inputs[0] * newWeightsDelta * alpha);
        firstNeuron.weights.set(1, firstNeuron.weights.get(1) - inputs[1] * newWeightsDelta * alpha);
        firstNeuron.weights.set(2, firstNeuron.weights.get(2) - inputs[2] * newWeightsDelta * alpha);
    }

    private void updateSecondNeuron(double weightsDelta, final double alpha, final double[] inputs) {
        double actual = secondNeuron.resultOfActiveFunction;
        double error = thirdNeuron.weights.get(1) * weightsDelta;
        double gradient = actual * (1 - actual);
        double newWeightsDelta = error * gradient;
        secondNeuron.weights.set(0, secondNeuron.weights.get(0) - inputs[0] * newWeightsDelta * alpha);
        secondNeuron.weights.set(1, secondNeuron.weights.get(1) - inputs[1] * newWeightsDelta * alpha);
        secondNeuron.weights.set(2, secondNeuron.weights.get(2) - inputs[2] * newWeightsDelta * alpha);
    }

    private void train(final double alpha, final double[] inputs) {
        getResultOfActiveFunction(inputs);
        double actual = thirdNeuron.resultOfActiveFunction;
        int y = actual >= 0.5 ? 1 : 0;
        if (y != inputs[3]) {
            globalError = true;
            double error = actual - inputs[3];
            double gradientLayer2 = actual * (1 - actual);
            double weightsDelta = round(error * gradientLayer2);
            updateThirdNeuron(weightsDelta, alpha);
            updateFirstNeuron(weightsDelta, alpha, inputs);
            updateSecondNeuron(weightsDelta, alpha, inputs);
        }
    }

    private ArrayList<double[]> getTrainingSet() {
        ArrayList<double[]> finalArrayList = new ArrayList<>();
        double[] doubleArr = new double[4];
        doubleArr[0] = 0;
        doubleArr[1] = 1;
        doubleArr[2] = 1;
        doubleArr[3] = 1;
        finalArrayList.add(doubleArr);
        doubleArr = new double[4];
        doubleArr[0] = 1;
        doubleArr[1] = 0;
        doubleArr[2] = 1;
        doubleArr[3] = 1;
        finalArrayList.add(doubleArr);
        doubleArr = new double[4];
        doubleArr[0] = 1;
        doubleArr[1] = 1;
        doubleArr[2] = 1;
        doubleArr[3] = 1;
        finalArrayList.add(doubleArr);
        doubleArr = new double[4];
        doubleArr[0] = 0;
        doubleArr[1] = 0;
        doubleArr[2] = 1;
        doubleArr[3] = 1;
        finalArrayList.add(doubleArr);
        doubleArr = new double[4];
        doubleArr[0] = 1;
        doubleArr[1] = 0;
        doubleArr[2] = 0;
        doubleArr[3] = 0;
        finalArrayList.add(doubleArr);
        doubleArr = new double[4];
        doubleArr[0] = 1;
        doubleArr[1] = 0.2;
        doubleArr[2] = 0.4;
        doubleArr[3] = 0;
        finalArrayList.add(doubleArr);
        doubleArr = new double[4];
        doubleArr[0] = 0;
        doubleArr[1] = 0.2;
        doubleArr[2] = 0.6;
        doubleArr[3] = 1;
        finalArrayList.add(doubleArr);
        doubleArr = new double[4];
        doubleArr[0] = 0;
        doubleArr[1] = 0.2;
        doubleArr[2] = 0.3;
        doubleArr[3] = 0;
        finalArrayList.add(doubleArr);
        doubleArr = new double[4];
        doubleArr[0] = 0;
        doubleArr[1] = 0.8;
        doubleArr[2] = 0.3;
        doubleArr[3] = 0;
        finalArrayList.add(doubleArr);
        doubleArr = new double[4];
        doubleArr[0] = 0;
        doubleArr[1] = 0.8;
        doubleArr[2] = 0.95;
        doubleArr[3] = 1;
        finalArrayList.add(doubleArr);
        doubleArr = new double[4];
        doubleArr[0] = 1;
        doubleArr[1] = 0.8;
        doubleArr[2] = 0.95;
        doubleArr[3] = 1;
        finalArrayList.add(doubleArr);
        doubleArr = new double[4];
        doubleArr[0] = 1;
        doubleArr[1] = 0.5;
        doubleArr[2] = 0.2;
        doubleArr[3] = 0;
        finalArrayList.add(doubleArr);
        doubleArr = new double[4];
        doubleArr[0] = 1;
        doubleArr[1] = 0.6;
        doubleArr[2] = 0.7;
        doubleArr[3] = 1;
        finalArrayList.add(doubleArr);

        return finalArrayList;
    }
}
