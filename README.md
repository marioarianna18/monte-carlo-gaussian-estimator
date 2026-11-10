# Monte Carlo Gaussian Area Estimator

A Java Swing application that estimates the area under a Gaussian (Normal Distribution) probability density function (PDF) using the **Monte Carlo stocastic integration method**, featuring real-time visual rendering.

![Java Swing Simulation Preview](https://img.shields.io/badge/Java-Swing-orange?style=flat-square) ![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)

---

## Features
* **Stocastic Estimation:** Uses random sampling within a bounding box to approximate definite integrals.
* **Customizable Parameters:** Dynamically adjust the total number of sampling points, standard deviation ($\sigma$), and mean ($\mu$).
* **Real-time Rendering:** Built with a safe custom `JPanel` rendering pipeline to prevent UI flickering and handle window resizing smoothly.
* **Object-Oriented Design:** Clean architectural separation between mathematical logic (`Gaussian`), UI controls (`GaussianMonteCarloFrame`), and graphical simulation panel (`SimulationPanel`).

---

## Mathematical Background

The probability density function (PDF) of a Gaussian distribution is given by:

$$f(x) = \frac{1}{\sigma \sqrt{2\pi}} e^{-\frac{(x - \mu)^2}{2\sigma^2}}$$

The simulation bounds are automatically configured to cover $\mu \pm 3\sigma$ (capturing $\approx 99.7\%$ of the distribution area). Points are randomly generated within the bounding rectangle, and the area is estimated by the ratio of points falling under the curve versus total points generated.

---

## 🛠️ How to Run

1. Clone or download this repository.
2. Open the project in your favorite Java IDE (e.g., NetBeans, IntelliJ IDEA, or Eclipse).
3. Ensure both `Gaussian.java` and `GaussianMonteCarloFrame.java` are inside the `montecarlogaussiana` package folder.
4. Run `GaussianMonteCarloFrame.java` as the main application.
