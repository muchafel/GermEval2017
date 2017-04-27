package de.germeval2017.Evaluation.EvaluationUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


public class EvaluationData<T> implements Iterable<EvaluationEntry<T>> {

	private List<EvaluationEntry<T>> entries;
	private UUID id;

	public EvaluationData() {
		this.entries = new ArrayList<>();
	}

	@SafeVarargs
	public EvaluationData(Iterable<EvaluationEntry<T>>... entries) {
		this.entries = new ArrayList<>();

		for (Iterable<EvaluationEntry<T>> e : entries) {
			Iterator<EvaluationEntry<T>> iterator = e.iterator();
			while (iterator.hasNext()) {
				this.entries.add(iterator.next());
			}
		}
	}

	public void register(T gold, T predicted) {
		EvaluationEntry<T> entry = new EvaluationEntry<T>(gold, predicted);
		entries.add(entry);
	}

	@Override
	public Iterator<EvaluationEntry<T>> iterator() {
		return new Iterator<EvaluationEntry<T>>() {

			List<EvaluationEntry<T>> data = new ArrayList<>(entries);
			int idx = 0;

			@Override
			public boolean hasNext() {
				return idx < data.size();
			}

			@Override
			public EvaluationEntry<T> next() {
				return data.get(idx++);
			}
		};
	}

	public long size() {
		return entries.size();
	}

	public EvaluationEntry<T> get(int idx) {
		return entries.get(idx);
	}

	public UUID getId() {
		if (id == null) {
			id = UUID.randomUUID();
		}
		return id;
	}

	public void registerBulk(Iterable<EvaluationEntry<T>> data) {
		Iterator<EvaluationEntry<T>> iterator = data.iterator();
		while (iterator.hasNext()) {
			EvaluationEntry<T> next = iterator.next();
			register(next.getGold(), next.getPredicted());
		}
	}

}
