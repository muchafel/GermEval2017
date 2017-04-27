package de.germeval2017.Evaluation.EvaluationUtil;

public class EvaluationEntry<T> {
	private T gold;
	private T predicted;

	public EvaluationEntry(T gold, T predicted) {
		this.gold = gold;
		this.predicted = predicted;
	}

	public T getGold() {
		return gold;
	}

	public void setGold(T gold) {
		this.gold = gold;
	}

	public T getPredicted() {
		return predicted;
	}

	public void setPredicted(T predicted) {
		this.predicted = predicted;
	}

	@Override
	public String toString() {
		return gold.toString() + "\t" + predicted.toString();
	}
}
